package moe.feng.danmaqua.data

import android.app.Application
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import moe.feng.danmaqua.model.BlockedUserRule
import moe.feng.danmaqua.model.Subscription

@Database(entities = [
    Subscription::class,
    BlockedUserRule::class
], version = 2)
abstract class DanmaquaDB : RoomDatabase() {

    companion object {

        const val DATABASE_NAME = "danmaqua"

        lateinit var instance: DanmaquaDB

        fun init(context: Context) {
            val appContext = if (context is Application) context else context.applicationContext
            if (!::instance.isInitialized) {
                instance = Room.databaseBuilder(
                    appContext,
                    DanmaquaDB::class.java,
                    DATABASE_NAME
                ).addMigrations(object : Migration(1, 2) {
                    override fun migrate(database: SupportSQLiteDatabase) {
                        database.execSQL("CREATE TABLE IF NOT EXISTS `blocked_user_rule` (`uid` INTEGER NOT NULL, `username` TEXT NOT NULL, `face` TEXT, PRIMARY KEY(`uid`))")
                    }
                }).build()
            }
        }

    }

    abstract fun subscriptions(): SubscriptionDao

    abstract fun blockedUsers(): BlockedUserRulesDao

}