package moe.feng.danmaqua.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import moe.feng.danmaqua.util.ext.readBool
import moe.feng.danmaqua.util.ext.writeBool

@Entity
data class Subscription(
    @PrimaryKey val uid: Long,
    @ColumnInfo(name = "room_id") val roomId: Long,
    @ColumnInfo val username: String,
    @ColumnInfo val avatar: String,
    @ColumnInfo var order: Int = 0,
    @ColumnInfo var selected: Boolean = false
) : Comparable<Subscription>, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readBool()
    )

    override fun compareTo(other: Subscription): Int {
        val distance = other.order - this.order
        if (distance == 0) {
            return this.username.compareTo(other.username)
        } else {
            return distance
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(uid)
        parcel.writeLong(roomId)
        parcel.writeString(username)
        parcel.writeString(avatar)
        parcel.writeInt(order)
        parcel.writeBool(selected)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Subscription> {
        override fun createFromParcel(parcel: Parcel): Subscription {
            return Subscription(parcel)
        }

        override fun newArray(size: Int): Array<Subscription?> {
            return arrayOfNulls(size)
        }
    }

}