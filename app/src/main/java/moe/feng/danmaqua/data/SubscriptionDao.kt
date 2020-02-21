package moe.feng.danmaqua.data

import androidx.room.*
import moe.feng.danmaqua.model.Subscription

@Dao
interface SubscriptionDao {

    @Insert
    suspend fun add(vararg items: Subscription)

    @Update
    suspend fun update(vararg items: Subscription)

    @Delete
    suspend fun delete(item: Subscription)

    @Query("SELECT COUNT(*) FROM subscription")
    suspend fun count(): Int

    @Query("SELECT * FROM subscription ORDER BY `order`")
    suspend fun getAll(): List<Subscription>

    @Query("SELECT * FROM subscription WHERE favourite = 1 ORDER BY `order`")
    suspend fun getFavourites(): List<Subscription>

    @Query("SELECT * FROM subscription WHERE selected = 1 LIMIT 1")
    suspend fun findSelected(): Subscription?

    @Query("SELECT * FROM subscription WHERE uid = :uid")
    suspend fun findByUid(uid: Long): Subscription?

    @Query("SELECT * FROM subscription WHERE room_id = :roomId")
    suspend fun findByRoomId(roomId: Long): Subscription?

}