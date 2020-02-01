package moe.feng.danmaqua.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class RoomDanmuConf(
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

    class Data(
        @SerializedName("refresh_row_factor") val refreshRowFactor: Float,
        @SerializedName("refresh_rate") val refreshRate: Int,
        @SerializedName("max_delay") val maxDelay: Int,
        val port: Int,
        val host: String,
        @SerializedName("host_server_list") val hostServerList: List<HostServerInfo>,
        @SerializedName("server_list") val serverList: List<ServerInfo>,
        val token: String
    ) : Parcelable {

        constructor(parcel: Parcel) : this(
            parcel.readFloat(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()!!,
            parcel.createTypedArrayList(HostServerInfo) ?: emptyList(),
            parcel.createTypedArrayList(ServerInfo) ?: emptyList(),
            parcel.readString()!!
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeFloat(refreshRowFactor)
            parcel.writeInt(refreshRate)
            parcel.writeInt(maxDelay)
            parcel.writeInt(port)
            parcel.writeString(host)
            parcel.writeTypedList(hostServerList)
            parcel.writeTypedList(serverList)
            parcel.writeString(token)
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

    class HostServerInfo(
        val host: String,
        val port: Int,
        @SerializedName("wss_port") val wssPort: Int,
        @SerializedName("ws_port") val wsPort: Int
    ) : Parcelable {

        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(host)
            parcel.writeInt(port)
            parcel.writeInt(wssPort)
            parcel.writeInt(wsPort)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<HostServerInfo> {
            override fun createFromParcel(parcel: Parcel): HostServerInfo {
                return HostServerInfo(parcel)
            }

            override fun newArray(size: Int): Array<HostServerInfo?> {
                return arrayOfNulls(size)
            }
        }

    }

    class ServerInfo(
        val host: String,
        val port: Int
    ) : Parcelable {

        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readInt()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(host)
            parcel.writeInt(port)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<ServerInfo> {
            override fun createFromParcel(parcel: Parcel): ServerInfo {
                return ServerInfo(parcel)
            }

            override fun newArray(size: Int): Array<ServerInfo?> {
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

    companion object CREATOR : Parcelable.Creator<RoomDanmuConf> {
        override fun createFromParcel(parcel: Parcel): RoomDanmuConf {
            return RoomDanmuConf(parcel)
        }

        override fun newArray(size: Int): Array<RoomDanmuConf?> {
            return arrayOfNulls(size)
        }
    }

}