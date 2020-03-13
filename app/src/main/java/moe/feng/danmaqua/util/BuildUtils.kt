package moe.feng.danmaqua.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.TAG
import java.io.IOException
import java.util.*

object BuildUtils {

    suspend fun getSystemProperty(propName: String): String? = withContext(Dispatchers.IO) {
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            p.inputStream.bufferedReader().use {
                return@withContext it.readLine()
            }
        } catch (ex: IOException) {
            Log.e(TAG, "Unable to read sysprop $propName", ex)
        }
        return@withContext null
    }

    suspend fun isFlymeOS(): Boolean {
        val displayId = getSystemProperty("ro.build.display.id")?.toLowerCase(Locale.getDefault())
        return !displayId.isNullOrEmpty() && displayId.contains("flyme")
    }

}