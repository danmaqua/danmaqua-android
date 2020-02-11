package moe.feng.danmaqua.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class RoomDanmuConf(
    val code: Int,
    val msg: String,
    val message: String,
    val data: Data
) : Parcelable {

    @Parcelize
    class Data(
        @SerializedName("refresh_row_factor") val refreshRowFactor: Float,
        @SerializedName("refresh_rate") val refreshRate: Int,
        @SerializedName("max_delay") val maxDelay: Int,
        val port: Int,
        val host: String,
        @SerializedName("host_server_list") val hostServerList: List<HostServerInfo>,
        @SerializedName("server_list") val serverList: List<ServerInfo>,
        val token: String
    ) : Parcelable

    @Parcelize
    class HostServerInfo(
        val host: String,
        val port: Int,
        @SerializedName("wss_port") val wssPort: Int,
        @SerializedName("ws_port") val wsPort: Int
    ) : Parcelable

    @Parcelize
    class ServerInfo(val host: String, val port: Int) : Parcelable

}