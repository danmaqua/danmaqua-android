package moe.feng.danmaqua.util

import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

object HttpUtils {

    var client: OkHttpClient = OkHttpClient()

    suspend fun <T> requestAsJson(request: Request, objClass: Class<T>): T = withContext(IO) {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val rawJson = response.body?.string()?.trim()
            if (rawJson?.isNotEmpty() != true) {
                throw IllegalJsonResponseException("Empty body", response)
            }
            return@withContext JsonUtils.fromJson(rawJson, objClass)
        } else {
            throw IllegalJsonResponseException("Request fail", response)
        }
    }

    suspend inline fun <reified T> requestAsJson(request: Request): T {
        return requestAsJson(request, T::class.java)
    }

}