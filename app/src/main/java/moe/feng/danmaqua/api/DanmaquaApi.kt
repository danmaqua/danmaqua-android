package moe.feng.danmaqua.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.model.Recommendation
import moe.feng.danmaqua.util.HttpUtils
import okhttp3.Request

object DanmaquaApi {

    const val API_HOST_CN = "https://danmaqua-cn.api.feng.moe"
    const val API_HOST_INTERNATIONAL = "https://danmaqua-intl.api.feng.moe"

    var apiHost: String = API_HOST_CN

    suspend fun getRecommendation(): Recommendation = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$apiHost/room/recommendation.json")
            .build()

        HttpUtils.requestAsJson<Recommendation>(request)
    }

}