package moe.feng.danmaqua.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.model.RoomInitInfo
import moe.feng.danmaqua.util.HttpUtils
import okhttp3.Request

object RoomApi {

    const val ROOM_INIT_URL = "https://api.live.bilibili.com/room/v1/Room/room_init?id=%d"

    // For Test
    const val MINATO_AQUA_ROOM_ID = 14917277L

    suspend fun getRoomInfo(id: Long): RoomInitInfo = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(ROOM_INIT_URL.format(id))
            .build()

        HttpUtils.requestAsJson<RoomInitInfo>(request)
    }

}