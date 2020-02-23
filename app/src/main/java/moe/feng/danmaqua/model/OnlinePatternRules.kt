package moe.feng.danmaqua.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OnlinePatternRules(
    val version: Int,
    val data: List<PatternRulesItem>
) : Parcelable
