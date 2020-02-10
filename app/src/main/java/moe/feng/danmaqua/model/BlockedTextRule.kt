package moe.feng.danmaqua.model

import com.google.gson.annotations.SerializedName

data class BlockedTextRule(
    val text: String,
    @SerializedName("is_regexp") var isRegExp: Boolean = false
)