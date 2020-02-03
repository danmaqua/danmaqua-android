package moe.feng.danmaqua

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.tencent.mmkv.MMKV
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
    }

}