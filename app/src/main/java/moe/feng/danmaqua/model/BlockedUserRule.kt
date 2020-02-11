package moe.feng.danmaqua.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_user_rule")
data class BlockedUserRule(
    @PrimaryKey val uid: Long,
    val username: String,
    val face: String? = null
)