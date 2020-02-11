package moe.feng.danmaqua.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoomInfo(
    val code: Int,
    val msg: String,
    val message: String,
    val data: Data
) : Parcelable {

    @Parcelize
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
    ) : Parcelable

}