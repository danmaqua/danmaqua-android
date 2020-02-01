package moe.feng.danmaqua.api

import moe.feng.danmaqua.model.RoomInitInfo
import moe.feng.danmaqua.util.HttpUtils
import okhttp3.Request

object RoomApi {

    const val ROOM_INIT_URL = "https://api.live.bilibili.com/room/v1/Room/room_init?id=%d"

    suspend fun getRoomInfo(id: Long): RoomInitInfo {
        val request = Request.Builder()
            .url(ROOM_INIT_URL.format(id))
            .build()
        return HttpUtils.requestAsJson(request)
    }

}