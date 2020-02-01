package moe.feng.danmaqua.api

import kotlinx.coroutines.*
import moe.feng.danmaqua.model.BiliChatMessage
import moe.feng.danmaqua.util.HttpUtils
import moe.feng.danmaqua.util.ext.toJson
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.nio.ByteBuffer
import java.util.*
import kotlin.concurrent.schedule

class DanmakuListener internal constructor(
    val roomId: Long,
    val callback: Callback,
    val address: String? = "broadcastlv.chat.bilibili.com"
) : WebSocketListener(), CoroutineScope by MainScope() {

    interface Callback {

        fun onConnect()

        fun onHeartbeat(online: Int)

        fun onMessage(msg: BiliChatMessage)

        fun onFailure(t: Throwable)

    }

    private var webSocket: WebSocket? = null
    private var heartbeatTimer: Timer? = null
    var isConnected: Boolean = false
        private set
    var isClosed: Boolean = false
        private set

    init {
        connect()
    }

    private fun connect() {
        if (roomId <= 0) {
            throw IllegalArgumentException("Non-positive room id")
        }
        if (isClosed) {
            throw IllegalStateException("DanmakuListener is closed. Please recreate a new one.")
        }
        val request = Request.Builder()
            .url("wss://$address/sub")
            .build()
        webSocket = HttpUtils.client.newWebSocket(request, this)
    }

    private fun heartbeat() {
        webSocket?.let {
            launch(Dispatchers.IO) {
                val heartbeatPacket = Protocol.encodePacket(Protocol.OP_HEARTBEAT)
                it.send(heartbeatPacket.toByteString())
            }
        }
    }

    private fun scheduleNextHeartbeat() {
        heartbeatTimer?.cancel()
        heartbeatTimer = Timer().also {
            it.schedule(30 * 1000) {
                heartbeat()
            }
        }
    }

    fun close() {
        if (isClosed) {
            throw IllegalStateException("DanmakuListener is closed.")
        }
        webSocket?.close(1000, null)
        heartbeatTimer?.cancel()
        isClosed = true
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        launch(Dispatchers.IO) {
            val joinPacket = Protocol.encodeJsonPacket(
                Protocol.OP_JOIN, Protocol.buildJoinMessageBody(roomId))
            webSocket.send(joinPacket.toByteString())
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        heartbeatTimer?.cancel()
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        onMessage(webSocket, bytes.asByteBuffer())
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        onMessage(webSocket, ByteBuffer.wrap(text.toByteArray()))
    }

    private fun onMessage(webSocket: WebSocket, byteBuffer: ByteBuffer) {
        launch(Dispatchers.IO) {
            for ((operation, _, data) in Protocol.decodePackets(byteBuffer)) {
                when (operation) {
                    Protocol.OP_WELCOME -> {
                        isConnected = true
                        heartbeat()
                        callback.onConnect()
                    }
                    Protocol.OP_HEARTBEAT_RESPONSE -> {
                        val online = data as? Int ?: (data as? Long)?.toInt() ?: 0
                        scheduleNextHeartbeat()
                        callback.onHeartbeat(online)
                    }
                    Protocol.OP_MESSAGE -> {
                        callback.onMessage(when (data) {
                            is Map<*, *> -> BiliChatMessage.from(data)
                            is String -> BiliChatMessage.from(data)
                            else -> BiliChatMessage.from(data.toJson())
                        })
                    }
                }
            }
        }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        close()
        callback.onFailure(t)
    }

}