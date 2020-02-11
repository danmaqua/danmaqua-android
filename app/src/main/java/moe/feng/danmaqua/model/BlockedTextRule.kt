package moe.feng.danmaqua.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BlockedTextRule(
    var text: String,
    @SerializedName("is_regexp") var isRegExp: Boolean = false
) : Parcelable