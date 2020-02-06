package moe.feng.danmaqua.util

import android.app.Activity
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.core.content.getSystemService
import androidx.core.net.toUri

object WindowUtils {

    /**
     * Check if application can draw over other apps
     * @param context Context
     * @return Boolean
     */
    fun canDrawOverlays(context: Context): Boolean {
        val sdkInt = Build.VERSION.SDK_INT
        if (sdkInt >= Build.VERSION_CODES.M) {
            if (sdkInt == Build.VERSION_CODES.O) {
                // Sometimes Settings.canDrawOverlays returns false after allowing permission.
                // Google Issue Tracker: https://issuetracker.google.com/issues/66072795
                val appOpsMgr: AppOpsManager = context.getSystemService() ?: return false
                val mode = appOpsMgr.checkOpNoThrow(
                    "android:system_alert_window",
                    Process.myUid(),
                    context.packageName
                )
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED
            }
            // Default
            return Settings.canDrawOverlays(context)
        }
        // This fallback may returns a incorrect result.
        return true
    }

    /**
     * Request overlay permission to draw over other apps
     * @param activity Current activity
     * @param requestCode Request code
     */
    fun requestOverlayPermission(activity: Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                "package:${activity.packageName}".toUri()
            )
            activity.startActivityForResult(intent, requestCode)
        }
        // TODO Support third-party customize ROM?
    }

}