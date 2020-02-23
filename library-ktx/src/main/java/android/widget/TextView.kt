package android.widget

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes

var TextView.compoundDrawableStart: Drawable?
    get() = compoundDrawablesRelative[0]
    set(value) {
        with (compoundDrawablesRelative) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(value, this[1], this[2], this[3])
        }
    }

var TextView.compoundDrawableStartRes: Int
    get() = throw UnsupportedOperationException()
    set(@DrawableRes value) {
        this.compoundDrawableStart = context.getDrawable(value)
    }