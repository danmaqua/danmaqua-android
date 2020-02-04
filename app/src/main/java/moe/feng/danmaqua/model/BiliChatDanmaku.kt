package moe.feng.danmaqua.model

import android.os.Parcel
import android.os.Parcelable

class BiliChatDanmaku(
    cmd: String,
    val text: String,
    val senderName: String,
    val senderUid: Long,
    timestamp: Long
) : BiliChatMessage(cmd, timestamp), Parcelable {

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<BiliChatDanmaku> {
            override fun createFromParcel(parcel: Parcel): BiliChatDanmaku {
                return BiliChatDanmaku(parcel)
            }

            override fun newArray(size: Int): Array<BiliChatDanmaku?> {
                return arrayOfNulls(size)
            }
        }

    }

    constructor(src: Parcel) : this(
        src.readString()!!,
        src.readString()!!,
        src.readString()!!,
        src.readLong(),
        src.readLong()
    )

    override fun toString(): String {
        return "BiliChatMessage.Danmaku[cmd: $cmd, " +
                "sender: $senderName($senderUid), " +
                "text: $text, " +
                "timestamp: $timestamp]"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(cmd)
            writeString(text)
            writeString(senderName)
            writeLong(senderUid)
            writeLong(timestamp)
        }
    }

}