package moe.feng.danmaqua.ui.list

import android.view.View
import androidx.annotation.IdRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseViewHolder(itemView: View)
    : RecyclerView.ViewHolder(itemView), CoroutineScope by MainScope() {

    fun <T : View> bindView(@IdRes id: Int): Lazy<T> {
        return lazy { itemView.findViewById<T>(id) }
    }

    fun onRecycled() {
        this.cancel()
    }

}