package moe.feng.danmaqua.util

import android.os.Build
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

    /**
     * It costs some time because it uses shell to get prop. So we cannot do it sync.
     */
    suspend fun isFlymeOS(): Boolean {
        val displayId = getSystemProperty("ro.build.display.id")?.toLowerCase(Locale.getDefault())
        return !displayId.isNullOrEmpty() && displayId.contains("flyme")
    }

    fun isMeizu(): Boolean {
        return Build.MANUFACTURER.toLowerCase(Locale.getDefault()).contains("meizu")
    }

}