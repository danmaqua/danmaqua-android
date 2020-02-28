package moe.feng.danmaqua.ui.common.list

import android.content.Context
import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

open class BaseViewHolder(itemView: View)
    : RecyclerView.ViewHolder(itemView), CoroutineScope by MainScope(), LayoutContainer {

    override val containerView: View = itemView

    val context: Context get() = itemView.context

    init {
        itemView.setOnClickListener {
            onItemClick()
        }
    }

    fun <T : View> bindView(@IdRes id: Int): Lazy<T> {
        return lazy { itemView.findViewById<T>(id) }
    }

    open fun onItemClick() {}

}