package moe.feng.danmaqua.util.ext

import android.content.Context
import moe.feng.common.eventshelper.EventsHelper

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