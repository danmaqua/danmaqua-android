package moe.feng.danmaqua.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewDelegate
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.list.SubscriptionManageButtonViewDelegate.Item
import moe.feng.danmaqua.ui.list.SubscriptionManageButtonViewDelegate.ViewHolder

class SubscriptionManageButtonViewDelegate(var callback: Callback? = null)
    : ItemViewDelegate<Item, ViewHolder>() {

    interface Callback {

        fun onSubscriptionManageClick()

    }

    object Item

    class ViewHolder(itemView: View) : BaseViewHolder(itemView)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.subscription_manage_item_view, parent, false))
            .apply { itemView.setOnClickListener { callback?.onSubscriptionManageClick() } }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Item) {}

}