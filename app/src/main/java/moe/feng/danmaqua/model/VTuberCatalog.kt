package moe.feng.danmaqua.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VTuberCatalog(
    val version: Int,
    val data: List<Group>
) : Parcelable {

    @Parcelize
    data class Group(
        val name: String,
        val title: String,
        val icon: String?,
        val count: Int
    ) : Parcelable

}