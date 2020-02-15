package moe.feng.danmaqua

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import moe.feng.danmaqua.api.bili.DanmakuApi
import moe.feng.danmaqua.api.bili.DanmakuListener
import moe.feng.danmaqua.model.BiliChatMessage
import moe.feng.danmaqua.util.ext.toJson
import org.junit.Test

class DanmakuApiTest {
    @Test
    fun test_getConf() {
        runBlocking {
            println(DanmakuApi.getConf(3).toJson())
        }
    }
    @Test
    fun test_listen() {
        runBlocking {
            DanmakuListener(1016, object : DanmakuListener.Callback {
                override fun onConnect() {
                    println("onConnect")
                }

                override fun onDisconnect(userReason: Boolean) {
                    println("onDisconnect: userReason=$userReason")
                }
                override fun onHeartbeat(online: Int) {
                    println("onHeartBeat: online=$online")
                }
                override fun onMessage(msg: BiliChatMessage) {
                    println("onMessage: $msg")
                }
                override fun onFailure(t: Throwable) {
                    throw t
                }
            }).connect()
            delay(60000)
        }
    }
}