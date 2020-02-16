package moe.feng.danmaqua.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VTuberGroup(
    val version: Int,
    val name: String,
    val title: String,
    val data: List<VTuberSingleItem>
) : Parcelable