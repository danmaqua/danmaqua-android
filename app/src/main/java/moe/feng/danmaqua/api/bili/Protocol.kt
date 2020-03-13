package moe.feng.danmaqua.api.bili

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.model.BiliChatPacket
import moe.feng.danmaqua.util.JsonUtils
import kotlinx.nio.currentSliceArray
import kotlinx.nio.getUInt
import kotlinx.nio.slice
import moe.feng.danmaqua.util.ext.toJson
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.zip.Inflater

object Protocol {

    const val PACKET_HEADER_LENGTH: Int = 16
    const val PACKET_SEQUENCE_ID: Int = 1

    const val PACKET_IDX_DATA_LENGTH = 0
    const val PACKET_IDX_HEADER_LENGTH = 4
    const val PACKET_IDX_PROTOCOL = 6
    const val PACKET_IDX_OPERATION = 8
    const val PACKET_IDX_SEQUENCE_ID = 12

    const val PROTOCOL_JSON = 0
    const val PROTOCOL_INT32BE = 1
    const val PROTOCOL_ZIP_BUFFER = 2

    const val OP_HEARTBEAT = 2
    const val OP_HEARTBEAT_RESPONSE = 3
    const val OP_MESSAGE = 5
    const val OP_JOIN = 7
    const val OP_WELCOME = 8

    suspend fun decodePackets(buffer: ByteBuffer): List<BiliChatPacket> = withContext(IO) {
        val realPacks = mutableListOf<BiliChatPacket>()
        var offset = 0
        while (offset < buffer.capacity()) {
            val size = buffer.getInt(offset)

            val pack = buffer.slice(offset, offset + size)

            val body = pack.slice(
                PACKET_HEADER_LENGTH
            )
            val protocol = pack.getShort(
                PACKET_IDX_PROTOCOL
            ).toInt()
            val operation = pack.getInt(
                PACKET_IDX_OPERATION
            )

            val data: Any = when {
                protocol == PROTOCOL_JSON ->
                    JsonUtils.fromJson(String(body.currentSliceArray()), Map::class.java)
                protocol == PROTOCOL_INT32BE && body.capacity() == 4 ->
                    body.getUInt(0).toLong()
                protocol == PROTOCOL_ZIP_BUFFER ->
                    decodePackets(
                        ByteBuffer.wrap(
                            zipInflate(body.currentSliceArray())
                        )
                    )
                else -> body
            }

            if (protocol == PROTOCOL_ZIP_BUFFER && data is List<*>) {
                realPacks.addAll(data as List<BiliChatPacket>)
            } else {
                realPacks += BiliChatPacket(operation, protocol, data)
            }

            offset += size
        }

        return@withContext realPacks.toList()
    }

    suspend fun encodePacket(operation: Int, protocol: Int = PROTOCOL_INT32BE,
                             body: ByteArray = byteArrayOf()): ByteArray = withContext(IO) {
        val header = ByteBuffer.allocate(
            PACKET_HEADER_LENGTH
        ).apply {
            putInt(PACKET_IDX_DATA_LENGTH, body.size + capacity())
            putShort(
                PACKET_IDX_HEADER_LENGTH, PACKET_HEADER_LENGTH.toShort())
            putShort(PACKET_IDX_PROTOCOL, protocol.toShort())
            putInt(PACKET_IDX_OPERATION, operation)
            putInt(
                PACKET_IDX_SEQUENCE_ID,
                PACKET_SEQUENCE_ID
            )
        }

        val bos = ByteArrayOutputStream().apply {
            write(header.array())
            write(body)
        }
        return@withContext bos.toByteArray()
    }

    suspend fun encodeJsonPacket(operation: Int, body: Any): ByteArray = withContext(IO) {
        val json = if (body is String) body else body.toJson()
        return@withContext encodePacket(
            operation,
            PROTOCOL_JSON,
            json.toByteArray()
        )
    }

    suspend fun zipInflate(data: ByteArray): ByteArray = withContext(IO) {
        val inflater = Inflater()
        inflater.setInput(data)
        val outputStream = ByteArrayOutputStream(data.size)
        val buffer = ByteArray(1024)
        while (!inflater.finished()) {
            val count = inflater.inflate(buffer)
            outputStream.write(buffer, 0, count)
        }
        outputStream.close()
        return@withContext outputStream.toByteArray()
    }

    fun buildJoinMessageBody(roomId: Long): Map<String, *> {
        return mapOf(
            "uid" to 0,
            "roomid" to roomId,
            "protoover" to 2,
            "platform" to "web",
            "clientver" to "1.8.5",
            "type" to 2
        )
    }

}