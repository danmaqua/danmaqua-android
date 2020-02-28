package moe.feng.danmaqua.ui.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.main_drawer_bottom_item_button.*
import moe.feng.danmaqua.R

class DrawerBottomItemButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyle: Int = R.style.Widget_Danmaqua_DrawerBottomItemButton
) : LinearLayout(context, attrs, defStyleAttr, defStyle), LayoutContainer {

    override val containerView: View = this

    init {
        LayoutInflater.from(context)
            .inflate(R.layout.main_drawer_bottom_item_button, this)

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.DrawerBottomItemButton,
            defStyleAttr, defStyle)

        icon.setImageDrawable(a.getDrawable(R.styleable.DrawerBottomItemButton_android_icon))
        text1.text = a.getString(R.styleable.DrawerBottomItemButton_android_text)

        a.recycle()
    }

}