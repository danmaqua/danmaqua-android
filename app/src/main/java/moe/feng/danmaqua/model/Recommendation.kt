package moe.feng.danmaqua.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Recommendation(
    val version: Int,
    val data: List<Item>
) : Parcelable {

    @Parcelize
    data class Item(
        val name: String,
        val uid: Long,
        val room: Long,
        val face: String,
        val top: String?,
        val reason: String,
        val recommender: String
    ) : Parcelable

}