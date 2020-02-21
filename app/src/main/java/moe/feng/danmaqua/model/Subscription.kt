package moe.feng.danmaqua.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class Subscription(
    @PrimaryKey val uid: Long,
    @ColumnInfo(name = "room_id") val roomId: Long,
    @ColumnInfo val username: String,
    @ColumnInfo val avatar: String,
    @ColumnInfo var order: Int = 0,
    @ColumnInfo var selected: Boolean = false,
    @ColumnInfo var favourite: Boolean = false
) : Comparable<Subscription>, Parcelable {

    override fun compareTo(other: Subscription): Int {
        val distance = other.order - this.order
        if (distance == 0) {
            return this.username.compareTo(other.username)
        } else {
            return distance
        }
    }

}