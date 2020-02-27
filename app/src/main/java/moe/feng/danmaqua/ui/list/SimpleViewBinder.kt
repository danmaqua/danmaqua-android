package moe.feng.danmaqua.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewBinder

abstract class SimpleViewBinder<T, VH : RecyclerView.ViewHolder> : ItemViewBinder<T, VH>() {

    interface ViewHolderCreator<VH : RecyclerView.ViewHolder> {

        fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH

    }

    abstract val viewHolderCreator: ViewHolderCreator<VH>

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
        return viewHolderCreator.onCreateViewHolder(inflater, parent).also(::onViewHolderCreated)
    }

    override fun onBindViewHolder(holder: VH, item: T) {

    }

    open fun onViewHolderCreated(holder: VH) {

    }

}