package androidx.content

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.core.net.toUri
import com.google.androidbrowserhelper.trusted.TwaLauncher
import moe.feng.common.eventshelper.EventsHelper
import moe.feng.danmaqua.util.IntentUtils

val Context.eventsHelper: EventsHelper get() = EventsHelper.getInstance(this)

val Context.packageVersionName: String? get() = try {
    val pi = packageManager.getPackageInfo(packageName, 0)
    pi.versionName
} catch (e: Exception) {
    null
}

val Context.packageVersionCode: Int? get() = try {
    val pi = packageManager.getPackageInfo(packageName, 0)
    pi.versionCode
} catch (e: Exception) {
    null
}

fun Context.launchViewUrl(@StringRes urlRes: Int): Boolean {
    return launchViewUrl(getString(urlRes))
}

fun Context.launchViewUrl(url: String): Boolean {
    return launchViewUrl(url.toUri())
}

fun Context.launchViewUrl(uri: Uri): Boolean {
    try {
        TwaLauncher(this).launch(uri)
    } catch (e: Exception) {
        try {
            startActivity(IntentUtils.view(uri))
        } catch (e: Exception) {
            return false
        }
    }
    return true
}
