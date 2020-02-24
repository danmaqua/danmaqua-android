package moe.feng.danmaqua.data

import androidx.room.TypeConverter
import moe.feng.danmaqua.model.TextTranslation
import moe.feng.danmaqua.util.JsonUtils
import moe.feng.danmaqua.util.ext.toJson

class Converters {

    @TypeConverter
    fun toTextTranslation(value: String?): TextTranslation? {
        if (value == null) {
            return null
        }
        return JsonUtils.fromJson<TextTranslation>(value)
    }

    @TypeConverter
    fun fromTextTranslation(obj: TextTranslation?): String? {
        return obj?.toJson()
    }

}