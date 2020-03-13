package androidx.view

import android.util.DisplayMetrics
import android.view.Display

val Display.screenWidth: Int get() {
    val metrics = DisplayMetrics().apply(this::getMetrics)
    return metrics.widthPixels
}

val Display.screenHeight: Int get() {
    val metrics = DisplayMetrics().apply(this::getMetrics)
    return metrics.heightPixels
}
