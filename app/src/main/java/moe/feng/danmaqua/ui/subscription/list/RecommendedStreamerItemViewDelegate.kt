package moe.feng.danmaqua.ui.subscription.list

import android.view.View
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.databinding.RecommendedStreamerListItemBinding
import moe.feng.danmaqua.event.OnRecommendedStreamerItemClickListener
import moe.feng.danmaqua.model.Recommendation.Item
import moe.feng.danmaqua.ui.common.list.*
import moe.feng.danmaqua.ui.subscription.list.RecommendedStreamerItemViewDelegate.ViewHolder
import androidx.content.eventsHelper

class RecommendedStreamerItemViewDelegate :
    ItemBasedSimpleViewBinder<Item, ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = dataBindingViewHolderCreatorOf(R.layout.recommended_streamer_list_item)

    class ViewHolder(dataBinding: RecommendedStreamerListItemBinding)
        : DataBindingViewHolder<Item, RecommendedStreamerListItemBinding>(dataBinding) {

        fun onCardClick(view: View) {
            context.eventsHelper.of<OnRecommendedStreamerItemClickListener>()
                .onRecommendedStreamerItemClick(data)
        }

    }

}