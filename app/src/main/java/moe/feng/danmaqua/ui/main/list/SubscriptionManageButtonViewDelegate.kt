package moe.feng.danmaqua.ui.main.list

import android.view.View
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.list.BaseViewHolder
import moe.feng.danmaqua.ui.list.SimpleViewBinder
import moe.feng.danmaqua.ui.list.viewHolderCreatorOf
import moe.feng.danmaqua.ui.main.list.SubscriptionManageButtonViewDelegate.Item
import moe.feng.danmaqua.ui.main.list.SubscriptionManageButtonViewDelegate.ViewHolder

class SubscriptionManageButtonViewDelegate(var callback: Callback? = null)
    : SimpleViewBinder<Item, ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = viewHolderCreatorOf(R.layout.subscription_manage_item_view)

    interface Callback {

        fun onSubscriptionManageClick()

    }

    object Item

    class ViewHolder(itemView: View) : BaseViewHolder(itemView)

    override fun onViewHolderCreated(holder: ViewHolder) {
        holder.itemView.setOnClickListener { callback?.onSubscriptionManageClick() }
    }

}