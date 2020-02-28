package moe.feng.danmaqua.ui.common.list

import androidx.annotation.CallSuper
import androidx.databinding.ViewDataBinding
import moe.feng.danmaqua.BR

abstract class DataBindingViewHolder<T : Any, DB : ViewDataBinding>(val dataBinding: DB)
    : ItemBasedViewHolder<T>(dataBinding.root) {

    @CallSuper
    override fun onBind() {
        dataBinding.setVariable(BR.data, data)
        dataBinding.setVariable(BR.holder, this)
    }

}