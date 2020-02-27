package moe.feng.danmaqua.ui.main.list

import android.view.View
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.subscription_item_view.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.ui.list.ItemBasedSimpleViewBinder
import moe.feng.danmaqua.ui.list.ItemBasedViewHolder
import moe.feng.danmaqua.ui.list.viewHolderCreatorOf
import moe.feng.danmaqua.util.ext.avatarUrl

class SubscriptionItemViewDelegate(var callback: Callback? = null)
    : ItemBasedSimpleViewBinder<Subscription, SubscriptionItemViewDelegate.ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = viewHolderCreatorOf(R.layout.subscription_item_view)

    interface Callback {

        fun onSubscriptionItemClick(item: Subscription)

        fun onSubscriptionItemLongClick(item: Subscription)

    }

    class ViewHolder(itemView: View) : ItemBasedViewHolder<Subscription>(itemView) {

        override fun onBind() {
            usernameView.text = data.username

            itemView.setBackgroundResource(if (data.selected) {
                R.drawable.subscription_item_background_selected
            } else {
                R.drawable.subscription_item_background_normal
            })
            avatarRing.isVisible = data.selected

            avatarView.avatarUrl = data.avatar
        }

    }

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