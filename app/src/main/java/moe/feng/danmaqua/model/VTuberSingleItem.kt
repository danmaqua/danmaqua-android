package moe.feng.danmaqua.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VTuberSingleItem(
    val uid: Long,
    val room: Long,
    val name: String,
    val group: String,
    val description: String,
    val face: String
) : Parcelable