package moe.feng.danmaqua.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import moe.feng.danmaqua.model.Subscription

@Dao
interface SubscriptionDao {

    @Insert
    suspend fun add(vararg items: Subscription)

    @Delete
    suspend fun delete(item: Subscription)

    @Query("SELECT * FROM subscription")
    suspend fun getAll(): List<Subscription>

    @Query("SELECT * FROM subscription WHERE uid = :uid")
    suspend fun findByUid(uid: Long): Subscription?

    @Query("SELECT * FROM subscription WHERE room_id = :roomId")
    suspend fun findByRoomId(roomId: Long): Subscription?

}