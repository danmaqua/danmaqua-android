package moe.feng.danmaqua

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.getSystemService
import androidx.room.Room
import com.tencent.mmkv.MMKV
import moe.feng.danmaqua.Danmaqua.NOTI_CHANNEL_ID_STATUS
import moe.feng.danmaqua.data.DanmaquaDB

class DanmaquaApplication : Application() {

    companion object {

        const val DATABASE_NAME = "danmaqua"

        private lateinit var danmaquaDB: DanmaquaDB

        fun getDatabase(context: Context): DanmaquaDB {
            val appContext = if (context is Application) context else context.applicationContext
            if (!::danmaquaDB.isInitialized) {
                danmaquaDB = Room.databaseBuilder(
                    appContext,
                    DanmaquaDB::class.java,
                    DATABASE_NAME
                ).build()
            }
            return danmaquaDB
        }

    }

    override fun onCreate() {
        super.onCreate()

        MMKV.initialize(this)

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