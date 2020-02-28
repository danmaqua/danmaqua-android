package moe.feng.danmaqua.ui.main.list

import moe.feng.danmaqua.R
import moe.feng.danmaqua.databinding.SubscriptionItemViewBinding
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.ui.common.list.*

class SubscriptionItemViewDelegate(var callback: Callback? = null)
    : ItemBasedSimpleViewBinder<Subscription, SubscriptionItemViewDelegate.ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = dataBindingViewHolderCreatorOf(R.layout.subscription_item_view)

    interface Callback {

        fun onSubscriptionItemClick(item: Subscription)

        fun onSubscriptionItemLongClick(item: Subscription)

    }

    class ViewHolder(dataBinding: SubscriptionItemViewBinding) :
        DataBindingViewHolder<Subscription, SubscriptionItemViewBinding>(dataBinding)

    override fun onViewHolderCreated(holder: ViewHolder) {
        with (holder) {
            itemView.setOnClickListener {
                callback?.onSubscriptionItemClick(data)
            }
            itemView.setOnLongClickListener {
                callback?.onSubscriptionItemLongClick(data)
                true
            }
        }
    }

}