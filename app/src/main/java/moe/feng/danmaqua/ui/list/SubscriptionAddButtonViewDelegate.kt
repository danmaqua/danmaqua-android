package moe.feng.danmaqua.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewDelegate
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.list.SubscriptionAddButtonViewDelegate.Item
import moe.feng.danmaqua.ui.list.SubscriptionAddButtonViewDelegate.ViewHolder

class SubscriptionAddButtonViewDelegate(var callback: Callback? = null)
    : ItemViewDelegate<Item, ViewHolder>() {

    interface Callback {

        fun onSubscriptionAddClick()

    }

    object Item

    class ViewHolder(itemView: View) : BaseViewHolder(itemView)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.subscription_add_item_view, parent, false))
            .apply { itemView.setOnClickListener { callback?.onSubscriptionAddClick() } }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Item) {}

}