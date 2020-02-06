package moe.feng.danmaqua.util

import android.content.res.Resources
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

object ResourcesUtils {

    @ColorInt
    fun resolveColor(theme: Resources.Theme, @AttrRes attrId: Int): Int {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }

}