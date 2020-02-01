package moe.feng.danmaqua.model

import com.google.gson.annotations.SerializedName
import moe.feng.danmaqua.util.JsonUtils

object BiliChatMessage {

    const val CMD_SEND_GIFT = "SEND_GIFT"
    const val CMD_DANMAKU = "DANMU_MSG"
    const val CMD_ENTRY_EFFECT = "ENTRY_EFFECT"
    const val CMD_WELCOME_GUARD = "WELCOME_GUARD"

    fun from(json: String): Base {
        return from(JsonUtils.fromJson(json, Map::class.java))
    }

    fun from(map: Map<*, *>): Base {
        val command: String = map["cmd"] as? String ?: ""
        return when (command) {
            CMD_SEND_GIFT -> {
                val data = map["data"] as? Map<*, *> ?: emptyMap<String, Any>()
                SendGift(
                    command,
                    data["giftName"] as? String ?: "",
                    data["num"] as? Int ?: 0,
                    data["uname"] as? String ?: "",
                    data["face"] as? String ?: "",
                    data["uid"] as? Long ?: 0L,
                    data["timestamp"] as? Long ?: 0L,
                    data["giftId"] as? Int ?: 0,
                    data["giftType"] as? Int ?: 0,
                    data["action"] as? String ?: "",
                    data["super"] as? Int ?: 0,
                    data["super_gift_num"] as? Int ?: 0,
                    data["super_batch_gift_num"] as? Int ?: 0,
                    data["price"] as? Int ?: 0,
                    data["coin_type"] as? String ?: "",
                    data["total_coin"] as? Int ?: 0
                )
            }
            CMD_DANMAKU -> {
                val info = map["info"] as? List<*> ?: emptyList<Any>()
                val text = info[1] as? String ?: ""
                val senderInfo = info[2] as? List<*> ?: emptyList<Any>()
                val senderUid = senderInfo[0] as? Long ?: 0L
                val senderName = senderInfo[1] as? String ?: ""
                val tsInfo = info[9] as? Map<*, *> ?: emptyMap<String, Any>()
                Danmaku(
                    command,
                    text,
                    senderName,
                    senderUid,
                    tsInfo["ts"] as? Long ?: 0L
                )
            }
            else -> Base(command, 0L)
        }
    }

    open class Base(val cmd: String, val timestamp: Long)

    class SendGift(
        cmd: String,
        val giftName: String,
        @SerializedName("num") val giftNum: Int,
        @SerializedName("uname") val username: String,
        val face: String,
        val uid: Long,
        timestamp: Long,
        val giftId: Int,
        val giftType: Int,
        val action: String,
        @SerializedName("super") val superGift: Int,
        @SerializedName("super_gift_num") val superGiftNum: Int,
        @SerializedName("super_batch_gift_num") val superBatchGiftNum: Int,
        val price: Int,
        @SerializedName("coin_type") val coinType: String,
        @SerializedName("total_coin") val totalCoin: Int
    ) : Base(cmd, timestamp)

    class Danmaku(
        cmd: String,
        val text: String,
        val senderName: String,
        val senderUid: Long,
        timestamp: Long
    ) : Base(cmd, timestamp)

}