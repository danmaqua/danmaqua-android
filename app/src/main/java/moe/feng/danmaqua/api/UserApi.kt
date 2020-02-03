package moe.feng.danmaqua.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.model.SpaceInfo
import moe.feng.danmaqua.util.HttpUtils
import okhttp3.Request

object UserApi {

    const val SPACE_INFO_URL = "https://api.bilibili.com/x/space/acc/info?mid=%d"

    suspend fun getSpaceInfo(uid: Long): SpaceInfo = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(SPACE_INFO_URL.format(uid))
            .build()

        HttpUtils.requestAsJson<SpaceInfo>(request)
    }

}