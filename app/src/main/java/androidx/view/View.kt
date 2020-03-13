package androidx.view

import android.content.Context
import android.util.TypedValue
import android.widget.ImageView
import com.squareup.picasso.Picasso
import moe.feng.danmaqua.R

fun Float.dpToPx(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)
}

fun Float.spToPx(context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, this, context.resources.displayMetrics)
}

var ImageView.avatarUrl: String?
    get() = throw UnsupportedOperationException("ImageView#avatarUrl can only set")
    set(value) {
        if (value.isNullOrEmpty()) {
            setImageResource(R.drawable.avatar_placeholder_empty)
        } else {
            Picasso.get()
                .load(value)
                .placeholder(R.drawable.avatar_placeholder_empty)
                .into(this)
        }
    }
