package moe.feng.danmaqua.ui.common.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import moe.feng.danmaqua.R
import moe.feng.danmaqua.util.BuildUtils

/**
 * Resolution for Meizu old devices/os
 *
 * See also: https://github.com/android-in-china/Compatibility/issues/11
 */
class FixedTextInputEditText
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    override fun getHint(): CharSequence? {
        return try {
            if (BuildUtils.isMeizu() && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                try {
                    getHintHacky()
                } catch (ignored: Exception) {
                    super.getHint()
                }
            } else {
                super.getHint()
            }
        } catch (ignored: Exception) {
            null
        }
    }

    private fun getHintHacky(): CharSequence? {
        val hintField = TextView::class.java.getDeclaredField("mHint").apply {
            isAccessible = true
        }
        return hintField.get(this) as? CharSequence
    }

}