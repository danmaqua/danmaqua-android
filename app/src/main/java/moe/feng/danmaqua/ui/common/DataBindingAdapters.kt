package moe.feng.danmaqua.ui.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import moe.feng.danmaqua.model.TextTranslation
import androidx.view.avatarUrl

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

    @BindingAdapter("app:htmlText")
    @JvmStatic
    fun setHtmlText(textView: TextView, html: String?) {
        textView.text = html?.let {
            HtmlCompat.fromHtml(it, 0)
        }
    }

    @BindingAdapter("app:htmlText")
    @JvmStatic
    fun setHtmlText(textView: TextView, @StringRes htmlResource: Int) {
        setHtmlText(textView, textView.resources.getString(htmlResource))
    }

    @BindingAdapter("app:isVisible")
    @JvmStatic
    fun setVisible(view: View, isVisible: Boolean) {
        view.isVisible = isVisible
    }

    @BindingAdapter("app:isInvisible")
    @JvmStatic
    fun setInvisible(view: View, isInvisible: Boolean) {
        view.isInvisible = isInvisible
    }

    @BindingAdapter("app:isGone")
    @JvmStatic
    fun setGone(view: View, isGone: Boolean) {
        view.isGone = isGone
    }

}