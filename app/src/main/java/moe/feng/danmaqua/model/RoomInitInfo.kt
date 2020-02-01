package moe.feng.danmaqua.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class RoomInitInfo(
    val code: Int,
    val msg: String,
    val message: String,
    val data: Data
): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(Data::class.java.classLoader)!!
    )

    data class Data(
        @SerializedName("room_id") val roomId: Long,
        @SerializedName("short_id") val shortId: Long,
        val uid: Long,
        @SerializedName("need_p2p") val needP2P: Int,
        @SerializedName("is_hidden") val isHidden: Boolean,
        @SerializedName("is_locked") val isLocked: Boolean,
        @SerializedName("is_portrait") val isPortrait: Boolean,
        @SerializedName("live_status") val liveStatus: Int,
        @SerializedName("hidden_till") val hiddenTill: Long,
        @SerializedName("lock_till") val lockTill: Long,
        val encrypted: Boolean,
        @SerializedName("pwd_verified") val pwdVerified: Boolean,
        @SerializedName("live_time") val liveTime: Long,
        @SerializedName("room_shield") val roomShield: Int,
        @SerializedName("is_sp") val isSp: Int,
        @SerializedName("special_type") val specialType: Int
    ): Parcelable {

        constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readLong(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeLong(roomId)
            parcel.writeLong(shortId)
            parcel.writeLong(uid)
            parcel.writeInt(needP2P)
            parcel.writeByte(if (isHidden) 1 else 0)
            parcel.writeByte(if (isLocked) 1 else 0)
            parcel.writeByte(if (isPortrait) 1 else 0)
            parcel.writeInt(liveStatus)
            parcel.writeLong(hiddenTill)
            parcel.writeLong(lockTill)
            parcel.writeByte(if (encrypted) 1 else 0)
            parcel.writeByte(if (pwdVerified) 1 else 0)
            parcel.writeLong(liveTime)
            parcel.writeInt(roomShield)
            parcel.writeInt(isSp)
            parcel.writeInt(specialType)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Data> {
            override fun createFromParcel(parcel: Parcel): Data {
                return Data(parcel)
            }

            override fun newArray(size: Int): Array<Data?> {
                return arrayOfNulls(size)
            }
        }

    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(code)
        parcel.writeString(msg)
        parcel.writeString(message)
        parcel.writeParcelable(data, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoomInitInfo> {
        override fun createFromParcel(parcel: Parcel): RoomInitInfo {
            return RoomInitInfo(parcel)
        }

        override fun newArray(size: Int): Array<RoomInitInfo?> {
            return arrayOfNulls(size)
        }
    }

}