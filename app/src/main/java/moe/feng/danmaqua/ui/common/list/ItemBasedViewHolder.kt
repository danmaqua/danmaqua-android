package moe.feng.danmaqua.ui.common.list

import android.view.View

abstract class ItemBasedViewHolder<T : Any>(itemView: View) : BaseViewHolder(itemView) {

    lateinit var data: T

    fun bind(data: T) {
        this.data = data
        onBind()
    }

    fun bind(data: T, payloads: List<Any>) {
        this.data = data
        onBind(payloads)
    }

    open fun onBind() {

    }

    open fun onBind(payloads: List<Any>) {
        onBind()
    }

    open fun onRecycled() {

    }

}