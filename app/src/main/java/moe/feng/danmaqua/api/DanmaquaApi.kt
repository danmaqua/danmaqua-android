package moe.feng.danmaqua.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.model.Recommendation
import moe.feng.danmaqua.model.VTuberCatalog
import moe.feng.danmaqua.model.VTuberGroup
import moe.feng.danmaqua.util.HttpUtils
import okhttp3.Request

object DanmaquaApi {

    const val API_HOST_CN = "https://danmaqua-cn.api.feng.moe"
    const val API_HOST_INTERNATIONAL = "https://danmaqua-intl.api.feng.moe"

    suspend fun getRecommendation(): Recommendation = withContext(Dispatchers.IO) {
        val cnRequest = Request.Builder()
            .url("$API_HOST_CN/room/recommendation.json")
            .build()
        val intlRequest = Request.Builder()
            .url("$API_HOST_INTERNATIONAL/room/recommendation.json")
            .build()

        try {
            HttpUtils.requestAsJson<Recommendation>(intlRequest)
        } catch (e: Exception) {
            HttpUtils.requestAsJson<Recommendation>(cnRequest)
        }
    }

    suspend fun getVTuberCatalog(): VTuberCatalog = withContext(Dispatchers.IO) {
        val cnRequest = Request.Builder()
            .url("$API_HOST_CN/room/vtubers_catalog.json")
            .build()
        val intlRequest = Request.Builder()
            .url("$API_HOST_INTERNATIONAL/room/vtubers_catalog.json")
            .build()

        try {
            HttpUtils.requestAsJson<VTuberCatalog>(intlRequest)
        } catch (e: Exception) {
            HttpUtils.requestAsJson<VTuberCatalog>(cnRequest)
        }
    }

    suspend fun getVTuberGroup(name: String): VTuberGroup = withContext(Dispatchers.IO) {
        val cnRequest = Request.Builder()
            .url("$API_HOST_CN/room/vtubers/$name.json")
            .build()
        val intlRequest = Request.Builder()
            .url("$API_HOST_INTERNATIONAL/room/vtubers/$name.json")
            .build()

        try {
            HttpUtils.requestAsJson<VTuberGroup>(intlRequest)
        } catch (e: Exception) {
            HttpUtils.requestAsJson<VTuberGroup>(cnRequest)
        }
    }

}