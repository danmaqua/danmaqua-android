package moe.feng.danmaqua.ui.subscription.list

import android.view.View
import kotlinx.android.synthetic.main.recommended_streamer_list_item.*
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.OnRecommendedStreamerItemClickListener
import moe.feng.danmaqua.model.Recommendation
import moe.feng.danmaqua.ui.list.ItemBasedSimpleViewBinder
import moe.feng.danmaqua.ui.list.ItemBasedViewHolder
import moe.feng.danmaqua.ui.list.viewHolderCreatorOf
import moe.feng.danmaqua.ui.subscription.list.RecommendedStreamerItemViewDelegate.ViewHolder
import moe.feng.danmaqua.util.ext.avatarUrl
import moe.feng.danmaqua.util.ext.eventsHelper

class RecommendedStreamerItemViewDelegate :
    ItemBasedSimpleViewBinder<Recommendation.Item, ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = viewHolderCreatorOf(R.layout.recommended_streamer_list_item)

    class ViewHolder(itemView: View) : ItemBasedViewHolder<Recommendation.Item>(itemView) {

        init {
            cardView.setOnClickListener {
                it.context.eventsHelper.of<OnRecommendedStreamerItemClickListener>()
                    .onRecommendedStreamerItemClick(data)
            }
        }

        override fun onBind() {
            avatarView.avatarUrl = data.face
            nameText.text = data.name
            reasonText.text = data.reason
        }

    }

}