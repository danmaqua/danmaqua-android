package moe.feng.danmaqua.data

import androidx.room.*
import moe.feng.danmaqua.model.BlockedUserRule

@Dao
interface BlockedUserRulesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(vararg items: BlockedUserRule)

    @Update
    suspend fun update(vararg items: BlockedUserRule)

    @Delete
    suspend fun delete(item: BlockedUserRule)

    @Query("SELECT COUNT(*) FROM blocked_user_rule")
    suspend fun count(): Int

    @Query("SELECT * FROM blocked_user_rule")
    suspend fun getAll(): List<BlockedUserRule>

    @Query("SELECT * FROM blocked_user_rule WHERE uid = :uid")
    suspend fun findByUid(uid: Long): BlockedUserRule?

}