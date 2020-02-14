package moe.feng.danmaqua.api.bili

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.model.RoomInfo
import moe.feng.danmaqua.model.RoomInitInfo
import moe.feng.danmaqua.util.HttpUtils
import okhttp3.Request

object RoomApi {

    const val ROOM_INIT_URL = "https://api.live.bilibili.com/room/v1/Room/room_init?id=%d"

    const val ROOM_INFO_URL = "https://api.live.bilibili.com/room/v1/Room/get_info?id=%d"

    // For Test
    const val MINATO_AQUA_ROOM_ID = 14917277L

    suspend fun getRoomInitInfo(id: Long): RoomInitInfo = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(ROOM_INIT_URL.format(id))
            .build()

        HttpUtils.requestAsJson<RoomInitInfo>(request)
    }

    suspend fun getRoomInfo(id: Long): RoomInfo = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(ROOM_INFO_URL.format(id))
            .build()

        HttpUtils.requestAsJson<RoomInfo>(request)
    }

}