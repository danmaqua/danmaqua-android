package moe.feng.danmaqua

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import moe.feng.danmaqua.api.DanmakuApi
import moe.feng.danmaqua.api.DanmakuListener
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
        DanmakuApi.listen(1016, object : DanmakuListener.Callback {
            override fun onConnect() {
                println("onConnect")
            }
            override fun onHeartbeat(online: Int) {
                println("onHeartBeat: online=$online")
            }
            override fun onMessage(data: Any) {
                println(if (data is String) data else data.toString())
            }
        })
        runBlocking {
            delay(60000)
        }
    }
}