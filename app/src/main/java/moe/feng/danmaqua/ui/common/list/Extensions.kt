package moe.feng.danmaqua.ui.common.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
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

inline fun <reified VH : RecyclerView.ViewHolder> Any.innerViewHolderCreatorOf(
    @LayoutRes layoutId: Int
): SimpleViewBinder.ViewHolderCreator<VH> {
    val thisRef = this
    return object : SimpleViewBinder.ViewHolderCreator<VH> {
        override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
            val view = inflater.inflate(layoutId, parent, false)
            return VH::class.java.getDeclaredConstructor(thisRef::class.java, View::class.java)
                .newInstance(thisRef, view)
        }
    }
}

inline fun <reified DB : ViewDataBinding, reified VH : DataBindingViewHolder<*, DB>>
dataBindingViewHolderCreatorOf(
    @LayoutRes layoutId: Int
): SimpleViewBinder.ViewHolderCreator<VH> {
    return object : SimpleViewBinder.ViewHolderCreator<VH> {
        override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
            val db = DataBindingUtil.inflate<DB>(inflater, layoutId, parent, false)
            return VH::class.java.getDeclaredConstructor(DB::class.java).newInstance(db)
        }
    }
}

inline fun <reified DB : ViewDataBinding, reified VH : DataBindingViewHolder<*, DB>>
Any.innerDataBindingViewHolderCreatorOf(
    @LayoutRes layoutId: Int
): SimpleViewBinder.ViewHolderCreator<VH> {
    val thisRef = this
    return object : SimpleViewBinder.ViewHolderCreator<VH> {
        override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): VH {
            val db = DataBindingUtil.inflate<DB>(inflater, layoutId, parent, false)
            return VH::class.java.getDeclaredConstructor(thisRef.javaClass, DB::class.java)
                .newInstance(thisRef, db)
        }
    }
}
