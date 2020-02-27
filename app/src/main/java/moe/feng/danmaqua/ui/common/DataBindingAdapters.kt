package moe.feng.danmaqua.ui.common

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import moe.feng.danmaqua.model.TextTranslation
import moe.feng.danmaqua.util.ext.avatarUrl

object DataBindingAdapters {

    @BindingAdapter("app:avatarUrl")
    @JvmStatic
    fun setAvatarUrl(imageView: ImageView, url: String?) {
        imageView.avatarUrl = url
    }

    @BindingAdapter("android:text")
    @JvmStatic
    fun setText(textView: TextView, textTranslation: TextTranslation) {
        textView.text = textTranslation()
    }

}