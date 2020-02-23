package moe.feng.danmaqua.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PatternRulesItem (
    val id: String,
    val title: TextTranslation,
    val desc: TextTranslation,
    val committer: String,
    val pattern: String
) : Parcelable
