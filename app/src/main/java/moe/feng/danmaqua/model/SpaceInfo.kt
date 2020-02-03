package moe.feng.danmaqua.model

import com.google.gson.annotations.SerializedName

data class SpaceInfo(
    val code: Int,
    val message: String,
    val ttl: Int,
    val data: Data
) {

    data class Data(
        @SerializedName("mid") val uid: Long,
        val name: String,
        val sex: String,
        val face: String,
        val sign: String,
        val rank: Int,
        val level: Int,
        @SerializedName("jointime") val joinTime: Long,
        val moral: Long,
        val silence: Long,
        val birthday: String,
        val coins: Int,
        @SerializedName("fans_badge") val fansBadge: Boolean,
        val official: OfficialInfo,
        val vip: VipInfo,
        @SerializedName("is_followed") val isFollowed: Boolean,
        @SerializedName("top_photo") val topPhoto: String,
        val theme: Map<String, *>,
        @SerializedName("sys_notice") val sysNotice: Map<String, *>
    )

    data class OfficialInfo(
        val role: Int,
        val title: String,
        val desc: String,
        val type: Int
    )

    data class VipInfo(
        val type: Int,
        val status: Int,
        @SerializedName("theme_type") val themeType: Int
    )

}