package moe.feng.danmaqua.ui.list

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

abstract class BaseViewHolder(itemView: View)
    : RecyclerView.ViewHolder(itemView), CoroutineScope by MainScope(), LayoutContainer {

    override val containerView: View = itemView

    fun <T : View> bindView(@IdRes id: Int): Lazy<T> {
        return lazy { itemView.findViewById<T>(id) }
    }

}