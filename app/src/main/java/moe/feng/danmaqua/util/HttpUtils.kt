package moe.feng.danmaqua.util

import android.util.Log
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlinx.TAG
import okhttp3.*
import java.util.regex.Pattern

object HttpUtils {

    var client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(ForceHttpsInterceptor)
        .build()

    fun setCache(cache: Cache) {
        client = client.newBuilder().cache(cache).build()
    }

    suspend fun <T> requestAsJson(request: Request, objClass: Class<T>): T = withContext(IO) {
        val response = client.newCall(request).execute()
        Log.d(TAG, "Request url: ${request.url}")
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

    object ForceHttpsInterceptor : Interceptor {

        private val KNOWN_SUPPORTED_HOSTS = arrayOf(
            Pattern.compile("[\\s\\S]*\\.hdslb\\.com")
        )

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            if (!request.url.isHttps) {
                val host = request.url.host
                if (KNOWN_SUPPORTED_HOSTS.none { it.matcher(host).matches() }) {
                    Log.e(
                        TAG, "Attempt to change request ${request.url} " +
                                "with unsupported host to https."
                    )
                }
                val newUrl = request.url.newBuilder().scheme("https").build()
                return chain.proceed(request.newBuilder().url(newUrl).build())
            } else {
                return chain.proceed(request)
            }
        }

    }

}