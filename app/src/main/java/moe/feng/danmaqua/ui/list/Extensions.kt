package moe.feng.danmaqua.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

inline fun <reified VH : RecyclerView.ViewHolder> viewHolderCreatorOf(@LayoutRes layoutId: Int)
        : SimpleViewBinder.ViewHolderCreator<VH> {
    return object : SimpleViewBinder.ViewHolderCreator<VH> {
        override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
            val view = inflater.inflate(layoutId, parent, false)
            return VH::class.java.getDeclaredConstructor(View::class.java).newInstance(view)
        }
    }
}
