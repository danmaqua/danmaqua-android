package moe.feng.danmaqua

import kotlinx.coroutines.runBlocking
import moe.feng.danmaqua.api.RoomApi
import moe.feng.danmaqua.util.ext.toJson
import org.junit.Test

class RoomApiTest {

    @Test
    fun test_getRoomInit() {
        runBlocking {
            println(RoomApi.getRoomInfo(3).toJson())
        }
    }

}