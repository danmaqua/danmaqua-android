package moe.feng.danmaqua.ui.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewDelegate
import kotlinx.android.synthetic.main.danmaku_history_toggle_item.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.history.ToggleViewDelegate.*
import moe.feng.danmaqua.ui.list.BaseViewHolder

class ToggleViewDelegate(var callback: Callback? = null) : ItemViewDelegate<Item, ViewHolder>() {

    interface Callback {

        fun onToggle()

    }

    class Item(var value: Boolean)

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        override fun onItemClick() {
            callback?.onToggle()
        }

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.danmaku_history_toggle_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Item) {
        holder.switchView.isChecked = item.value
    }

}