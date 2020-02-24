package moe.feng.danmaqua.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "pattern_rules")
data class PatternRulesItem (
    @PrimaryKey val id: String,
    @ColumnInfo var title: TextTranslation = TextTranslation(),
    @ColumnInfo var desc: TextTranslation = TextTranslation(),
    @ColumnInfo var committer: String = "",
    @ColumnInfo var pattern: String = "",
    @ColumnInfo var local: Boolean = true,
    @ColumnInfo var selected: Boolean = false
) : Parcelable
