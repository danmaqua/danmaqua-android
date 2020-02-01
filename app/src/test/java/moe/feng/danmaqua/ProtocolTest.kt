package moe.feng.danmaqua

import kotlinx.coroutines.runBlocking
import moe.feng.danmaqua.api.Protocol
import org.junit.Assert.*
import org.junit.Test
import java.nio.ByteBuffer

class ProtocolTest {
    @Test
    fun validate_CoderProcess() = runBlocking {
        val body = mapOf("title" to "Test", "content" to "Hello, world!")
        val packet = Protocol.encodeJsonPacket(Protocol.OP_MESSAGE, body)
        val out = Protocol.decodePackets(ByteBuffer.wrap(packet))[0]
        val outData = out.data as? Map<String, String>
        assertTrue(out.operation == Protocol.OP_MESSAGE)
        assertTrue(out.protocol == Protocol.PROTOCOL_JSON)
        assertNotNull(outData)
        assertTrue(outData?.get("title") == "Test")
        assertTrue(outData?.get("content") == "Hello, world!")
    }
}