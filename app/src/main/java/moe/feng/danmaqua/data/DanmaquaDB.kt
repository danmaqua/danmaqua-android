package moe.feng.danmaqua.data

import androidx.room.Database
import androidx.room.RoomDatabase
import moe.feng.danmaqua.model.Subscription

@Database(entities = [Subscription::class], version = 1)
abstract class DanmaquaDB : RoomDatabase() {

    abstract fun subscriptions(): SubscriptionDao

}