package moe.feng.danmaqua.ui.main.list

import android.view.View
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.list.BaseViewHolder
import moe.feng.danmaqua.ui.list.SimpleViewBinder
import moe.feng.danmaqua.ui.list.viewHolderCreatorOf
import moe.feng.danmaqua.ui.main.list.SubscriptionAddButtonViewDelegate.Item
import moe.feng.danmaqua.ui.main.list.SubscriptionAddButtonViewDelegate.ViewHolder

class SubscriptionAddButtonViewDelegate(var callback: Callback? = null)
    : SimpleViewBinder<Item, ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = viewHolderCreatorOf(R.layout.subscription_add_item_view)

    interface Callback {

        fun onSubscriptionAddClick()

    }

    object Item

    class ViewHolder(itemView: View) : BaseViewHolder(itemView)

    override fun onViewHolderCreated(holder: ViewHolder) {
        holder.itemView.setOnClickListener { callback?.onSubscriptionAddClick() }
    }

}