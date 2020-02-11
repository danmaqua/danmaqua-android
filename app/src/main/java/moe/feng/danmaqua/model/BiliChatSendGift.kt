package moe.feng.danmaqua.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class BiliChatSendGift(
    override val cmd: String,
    val giftName: String,
    @SerializedName("num") val giftNum: Int,
    @SerializedName("uname") val username: String,
    val face: String,
    val uid: Long,
    override val timestamp: Long,
    val giftId: Int,
    val giftType: Int,
    val action: String,
    @SerializedName("super") val superGift: Int,
    @SerializedName("super_gift_num") val superGiftNum: Int,
    @SerializedName("super_batch_gift_num") val superBatchGiftNum: Int,
    val price: Int,
    @SerializedName("coin_type") val coinType: String,
    @SerializedName("total_coin") val totalCoin: Int
) : BiliChatMessage(cmd, timestamp), Parcelable {

    override fun toString(): String {
        return "BiliChatMessage.SendGift[cmd: $cmd, " +
                "sender: $username($uid), " +
                "gift: $giftName*$giftNum, " +
                "timestamp: $timestamp, " +
                "action: $action, " +
                "coin: $coinType*$totalCoin]"
    }

}