package moe.feng.danmaqua

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.StatFs
import androidx.core.content.getSystemService
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.tencent.mmkv.MMKV
import moe.feng.danmaqua.Danmaqua.NOTI_CHANNEL_ID_STATUS
import moe.feng.danmaqua.data.DanmaquaDB
import moe.feng.danmaqua.util.HttpUtils
import okhttp3.Cache
import java.io.File

class DanmaquaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

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
        val okHttpCache = Cache(okHttpCacheDir, cacheSize)
        HttpUtils.setCache(okHttpCache)

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
    }

}