package moe.feng.danmaqua.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class RoomInitInfo(
    val code: Int,
    val msg: String,
    val message: String,
    val data: Data
): Parcelable {

    companion object {

        const val LIVE_STATUS_CLOSED = 0
        const val LIVE_STATUS_ACTIVE = 1
        const val LIVE_STATUS_CAROUSEL = 2

    }

    @Parcelize
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
    ): Parcelable

}