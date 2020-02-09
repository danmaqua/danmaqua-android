package moe.feng.danmaqua.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class RoomInfo(
    val code: Int,
    val msg: String,
    val message: String,
    val data: Data
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(Data::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeInt(code)
            writeString(msg)
            writeString(message)
            writeParcelable(data, flags)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RoomInfo> {
        override fun createFromParcel(parcel: Parcel): RoomInfo {
            return RoomInfo(parcel)
        }

        override fun newArray(size: Int): Array<RoomInfo?> {
            return arrayOfNulls(size)
        }
    }

    data class Data(
        val uid: Long,
        @SerializedName("room_id") val roomId: Long,
        @SerializedName("short_id") val shortId: Int,
        val online: Int,
        val description: String,
        @SerializedName("parent_area_name") val parentAreaName: String,
        @SerializedName("area_name") val areaName: String,
        val title: String,
        @SerializedName("live_time") val liveTime: String,
        @SerializedName("live_status") val liveStatus: Int
    ) : Parcelable {

        constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            with(parcel) {
                writeLong(uid)
                writeLong(roomId)
                writeInt(shortId)
                writeInt(online)
                writeString(description)
                writeString(parentAreaName)
                writeString(areaName)
                writeString(title)
                writeString(liveTime)
                writeInt(liveStatus)
            }
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

}