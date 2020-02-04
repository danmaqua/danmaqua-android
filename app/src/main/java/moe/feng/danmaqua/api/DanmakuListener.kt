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
) : WebSocketListener(), CoroutineScope by CoroutineScope(SupervisorJob() + Dispatchers.IO) {

    interface Callback {

        fun onConnect()

        fun onDisconnect(userReason: Boolean)

        fun onHeartbeat(online: Int)

        fun onMessage(msg: BiliChatMessage)

        fun onFailure(t: Throwable)

    }

    private var webSocket: WebSocket? = null
    private var heartbeatTimer: Timer? = null
    private var userReasonClose: Boolean = false
    private var calledOnDisconnect: Boolean = false
    var isConnected: Boolean = false
        private set
    var isClosed: Boolean = false
        private set
    var realRoomId: Long = 0L
        private set

    init {
        launch {
            connect()
        }
    }

    private suspend fun connect() = withContext(Dispatchers.IO) {
        if (roomId <= 0) {
            throw IllegalArgumentException("Non-positive room id")
        }
        if (isClosed) {
            throw IllegalStateException("DanmakuListener is closed. Please recreate a new one.")
        }
        val roomInfo = RoomApi.getRoomInfo(roomId)
        realRoomId = roomInfo.data.roomId
        if (realRoomId <= 0) {
            throw IllegalStateException("Cannot get real room id")
        }
        val request = Request.Builder()
            .url("wss://$address/sub")
            .build()
        webSocket = HttpUtils.client.newWebSocket(request, this@DanmakuListener)
    }

    private suspend fun heartbeat() = withContext(Dispatchers.IO) {
        webSocket?.let {
            val heartbeatPacket = Protocol.encodePacket(Protocol.OP_HEARTBEAT)
            it.send(heartbeatPacket.toByteString())
        }
    }

    private fun scheduleNextHeartbeat() {
        heartbeatTimer?.cancel()
        heartbeatTimer = Timer().also {
            it.schedule(30 * 1000) {
                launch {
                    heartbeat()
                }
            }
        }
    }

    private fun callOnConnect() = launch(Dispatchers.Main) {
        callback.onConnect()
    }

    private fun callOnDisconnect(userReason: Boolean) = launch(Dispatchers.Main) {
        if (calledOnDisconnect) {
            return@launch
        }
        callback.onDisconnect(userReason)
        calledOnDisconnect = true
    }

    private fun callOnHeartbeat(online: Int) = launch(Dispatchers.Main) {
        callback.onHeartbeat(online)
    }

    private fun callOnMessage(msg: BiliChatMessage) = launch(Dispatchers.Main) {
        callback.onMessage(msg)
    }

    private fun callOnFailure(t: Throwable) = launch(Dispatchers.Main) {
        callback.onFailure(t)
    }

    private fun internalClose() {
        if (isClosed) {
            return
        }
        webSocket?.close(1000, null)
        heartbeatTimer?.cancel()
        isConnected = false
        isClosed = true
        callOnDisconnect(userReasonClose)
    }

    fun close() {
        if (isClosed) {
            throw IllegalStateException("DanmakuListener is closed.")
        }
        userReasonClose = true
        internalClose()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        launch(Dispatchers.IO) {
            val joinPacket = Protocol.encodeJsonPacket(
                Protocol.OP_JOIN, Protocol.buildJoinMessageBody(realRoomId))
            webSocket.send(joinPacket.toByteString())
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        heartbeatTimer?.cancel()
        isConnected = false
        callOnDisconnect(userReasonClose)
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
                        callOnConnect()
                    }
                    Protocol.OP_HEARTBEAT_RESPONSE -> {
                        val online = data as? Int ?: (data as? Long)?.toInt() ?: 0
                        scheduleNextHeartbeat()
                        callOnHeartbeat(online)
                    }
                    Protocol.OP_MESSAGE -> {
                        callOnMessage(when (data) {
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
        internalClose()
        callOnFailure(t)
    }

}