package moe.feng.danmaqua.api

import moe.feng.danmaqua.model.RoomDanmuConf
import moe.feng.danmaqua.util.HttpUtils
import okhttp3.Request

object DanmakuApi {

    const val GET_CONF_URL = "https://api.live.bilibili.com/room/v1/Danmu/getConf?room_id=%d"

    suspend fun getConf(roomId: Long): RoomDanmuConf {
        val request = Request.Builder()
            .url(GET_CONF_URL.format(roomId))
            .build()
        return HttpUtils.requestAsJson(request)
    }

    fun listen(roomId: Long, callback: DanmakuListener.Callback): DanmakuListener {
        return DanmakuListener(roomId, callback)
    }

}