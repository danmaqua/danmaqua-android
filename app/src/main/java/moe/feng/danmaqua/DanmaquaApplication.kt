package moe.feng.danmaqua

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.StatFs
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.getSystemService
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.perf.FirebasePerformance
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.tencent.mmkv.MMKV
import moe.feng.common.eventshelper.EventsHelper
import moe.feng.danmaqua.Danmaqua.NOTI_CHANNEL_ID_STATUS
import moe.feng.danmaqua.data.DanmaquaDB
import moe.feng.danmaqua.event.SettingsChangedListener
import moe.feng.danmaqua.util.HttpUtils
import kotlinx.TAG
import androidx.content.eventsHelper
import moe.feng.danmaqua.work.UpdatePatternRulesWork
import okhttp3.Cache
import java.io.File

class DanmaquaApplication : Application(), SettingsChangedListener {

    private var firebaseSdkEnabled: Boolean = true
    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(this) }
    private val firebaseCrashlytics by lazy { FirebaseCrashlytics.getInstance() }
    private val firebasePerf by lazy { FirebasePerformance.getInstance() }

    override fun onCreate() {
        super.onCreate()

        initComponents()
        UpdatePatternRulesWork.enqueue(this)

        updateSdkEnabled()
        AppCompatDelegate.setDefaultNightMode(Danmaqua.Settings.darkMode)

        eventsHelper.registerListener(this)
    }

    override fun onSettingsChanged() {
        updateSdkEnabled()
    }

    private fun initComponents() {
        val okHttpCacheDir = File(cacheDir, "okhttp")
        var cacheSize = 5 * 1024 * 1024L
        try {
            val statFs = StatFs(okHttpCacheDir.absolutePath)
            val blockCount = statFs.blockCountLong
            val blockSize = statFs.blockSizeLong
            val available = blockCount * blockSize
            // Target 2% of the total space.
            cacheSize = available / 50
        } catch (ignored: IllegalArgumentException) {
        }
        val okHttpCache = Cache(okHttpCacheDir, cacheSize.coerceAtLeast(1))
        HttpUtils.setCache(okHttpCache)

        EventsHelper.getInstance(this)
        DanmaquaDB.init(this)
        MMKV.initialize(this)
        Picasso.setSingletonInstance(Picasso.Builder(this)
            .downloader(OkHttp3Downloader(HttpUtils.client))
            .build())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService<NotificationManager>()!!
            val channel = NotificationChannel(
                NOTI_CHANNEL_ID_STATUS,
                getString(R.string.noti_channel_status),
                NotificationManager.IMPORTANCE_MIN
            )
            manager.createNotificationChannel(channel)
        }

        Danmaqua.Settings.transferPatternSettingsToNewDB(this)
    }

    private fun updateSdkEnabled() {
        val newState = Danmaqua.Settings.enabledAnalytics
        if (newState != firebaseSdkEnabled) {
            Log.d(TAG, "Firebase SDK status changed to enabled=$newState")
            firebaseAnalytics.setAnalyticsCollectionEnabled(newState)
            firebaseCrashlytics.setCrashlyticsCollectionEnabled(newState)
            if (!BuildConfig.DEBUG) {
                firebasePerf.isPerformanceCollectionEnabled = newState
            }
            firebaseSdkEnabled = newState
        }
    }

}