package moe.feng.danmaqua.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class BiliChatSendGift(
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
) : BiliChatMessage(cmd, timestamp), Parcelable {

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<BiliChatSendGift> {
            override fun createFromParcel(parcel: Parcel): BiliChatSendGift {
                return BiliChatSendGift(parcel)
            }

            override fun newArray(size: Int): Array<BiliChatSendGift?> {
                return arrayOfNulls(size)
            }
        }

    }

    constructor(src: Parcel) : this(
        src.readString()!!,
        src.readString()!!,
        src.readInt(),
        src.readString()!!,
        src.readString()!!,
        src.readLong(),
        src.readLong(),
        src.readInt(),
        src.readInt(),
        src.readString()!!,
        src.readInt(),
        src.readInt(),
        src.readInt(),
        src.readInt(),
        src.readString()!!,
        src.readInt()
    )

    override fun toString(): String {
        return "BiliChatMessage.SendGift[cmd: $cmd, " +
                "sender: $username($uid), " +
                "gift: $giftName*$giftNum, " +
                "timestamp: $timestamp, " +
                "action: $action, " +
                "coin: $coinType*$totalCoin]"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(cmd)
            writeString(giftName)
            writeInt(giftNum)
            writeString(username)
            writeString(face)
            writeLong(uid)
            writeLong(timestamp)
            writeInt(giftId)
            writeInt(giftType)
            writeString(action)
            writeInt(superGift)
            writeInt(superGiftNum)
            writeInt(superBatchGiftNum)
            writeInt(price)
            writeString(coinType)
            writeInt(totalCoin)
        }
    }

}