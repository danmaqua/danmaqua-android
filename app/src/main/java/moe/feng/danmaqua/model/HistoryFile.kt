package moe.feng.danmaqua.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.File
import java.util.*

@Parcelize
data class HistoryFile(
    val roomId: Long,
    val year: Int,
    val month: Int,
    val day: Int,
    val file: File
) : Parcelable {

    fun getDate(): Calendar {
        return Calendar.getInstance().also {
            it.set(Calendar.YEAR, year)
            it.set(Calendar.MONTH, month - 1)
            it.set(Calendar.DAY_OF_MONTH, day)
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other as? HistoryFile)?.file == this.file
    }

    override fun hashCode(): Int {
        var result = roomId.hashCode()
        result = 31 * result + year
        result = 31 * result + month
        result = 31 * result + day
        result = 31 * result + file.hashCode()
        return result
    }

}