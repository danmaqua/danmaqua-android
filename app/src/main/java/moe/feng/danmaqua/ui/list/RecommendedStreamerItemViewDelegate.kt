package moe.feng.danmaqua.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewDelegate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recommended_streamer_list_item.*
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.OnRecommendedStreamerItemClickListener
import moe.feng.danmaqua.model.Recommendation
import moe.feng.danmaqua.util.ext.eventsHelper

class RecommendedStreamerItemViewDelegate :
    ItemViewDelegate<Recommendation.Item, RecommendedStreamerItemViewDelegate.ViewHolder>() {

    class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        lateinit var item: Recommendation.Item

        init {
            cardView.setOnClickListener {
                it.context.eventsHelper.of<OnRecommendedStreamerItemClickListener>()
                    .onRecommendedStreamerItemClick(item)
            }
        }

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.recommended_streamer_list_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Recommendation.Item) {
        with(holder) {
            this.item = item

            Picasso.get()
                .load(item.face)
                .placeholder(R.drawable.avatar_placeholder_empty)
                .into(avatarView)

            nameText.text = item.name
            reasonText.text = item.reason
        }
    }

}