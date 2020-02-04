package moe.feng.danmaqua.model

import android.os.Parcel
import android.os.Parcelable
import moe.feng.danmaqua.util.JsonUtils

open class BiliChatMessage(val cmd: String, val timestamp: Long) : Parcelable {

    companion object {

        const val CMD_SEND_GIFT = "SEND_GIFT"
        const val CMD_DANMAKU = "DANMU_MSG"
        const val CMD_ENTRY_EFFECT = "ENTRY_EFFECT"
        const val CMD_WELCOME_GUARD = "WELCOME_GUARD"

        fun from(json: String): BiliChatMessage {
            return from(JsonUtils.fromJson(json, Map::class.java))
        }

        fun from(map: Map<*, *>): BiliChatMessage {
            val command: String = map["cmd"] as? String ?: ""
            return when (command) {
                CMD_SEND_GIFT -> {
                    val data = map["data"] as? Map<*, *> ?: emptyMap<String, Any>()
                    BiliChatSendGift(
                        command,
                        data["giftName"] as? String ?: "",
                        (data["num"] as? Number)?.toInt() ?: 0,
                        data["uname"] as? String ?: "",
                        data["face"] as? String ?: "",
                        (data["uid"] as? Number)?.toLong() ?: 0L,
                        (data["timestamp"] as? Number)?.toLong() ?: 0L,
                        (data["giftId"] as? Number)?.toInt() ?: 0,
                        (data["giftType"] as? Number)?.toInt() ?: 0,
                        data["action"] as? String ?: "",
                        (data["super"] as? Number)?.toInt() ?: 0,
                        (data["super_gift_num"] as? Number)?.toInt() ?: 0,
                        (data["super_batch_gift_num"] as? Number)?.toInt() ?: 0,
                        (data["price"] as? Number)?.toInt() ?: 0,
                        data["coin_type"] as? String ?: "",
                        (data["total_coin"] as? Number)?.toInt() ?: 0
                    )
                }
                CMD_DANMAKU -> {
                    val info = map["info"] as? List<*> ?: emptyList<Any>()
                    val text = info[1] as? String ?: ""
                    val senderInfo = info[2] as? List<*> ?: emptyList<Any>()
                    val senderUid = senderInfo[0] as? Long ?: 0L
                    val senderName = senderInfo[1] as? String ?: ""
                    val tsInfo = info[9] as? Map<*, *> ?: emptyMap<String, Any>()
                    BiliChatDanmaku(
                        command,
                        text,
                        senderName,
                        senderUid,
                        (tsInfo["ts"] as? Number)?.toLong() ?: 0L
                    )
                }
                else -> BiliChatMessage(command, 0L)
            }
        }

        @JvmField
        val CREATOR = object : Parcelable.Creator<BiliChatMessage> {
            override fun createFromParcel(parcel: Parcel): BiliChatMessage {
                return BiliChatMessage(parcel)
            }

            override fun newArray(size: Int): Array<BiliChatMessage?> {
                return arrayOfNulls(size)
            }
        }

    }

    constructor(src: Parcel) : this(
        src.readString()!!,
        src.readLong()
    )

    override fun toString(): String {
        return "BiliChatMessage[cmd: $cmd, timestamp: $timestamp, (Unrecognized data)]"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(cmd)
            writeLong(timestamp)
        }
    }

    override fun describeContents(): Int {
        return 0
    }

}