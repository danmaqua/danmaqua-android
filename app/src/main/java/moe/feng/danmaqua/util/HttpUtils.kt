package moe.feng.danmaqua.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.util.ext.TAG
import okhttp3.*
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

object HttpUtils {

    var client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(ForceHttpsInterceptor)
        .build()

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

    suspend fun loadBitmapWithCache(url: String): Bitmap? = withContext(IO) {
        val request = Request.Builder()
            .cacheControl(CacheControl.Builder().maxAge(1800, TimeUnit.SECONDS).build())
            .url(url)
            .build()
        Log.d(TAG, "Request url: $url")
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val input = response.body?.byteStream() ?: return@withContext null
                return@withContext BitmapFactory.decodeStream(input)

            } else {
                return@withContext null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
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