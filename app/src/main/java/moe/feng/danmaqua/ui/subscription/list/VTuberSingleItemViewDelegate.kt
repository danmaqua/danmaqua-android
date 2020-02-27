package moe.feng.danmaqua.ui.subscription.list

import android.view.View
import kotlinx.android.synthetic.main.view_vtubers_single_item.*
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.OnCatalogSingleItemClickListener
import moe.feng.danmaqua.model.VTuberSingleItem
import moe.feng.danmaqua.ui.list.ItemBasedSimpleViewBinder
import moe.feng.danmaqua.ui.list.ItemBasedViewHolder
import moe.feng.danmaqua.ui.list.viewHolderCreatorOf
import moe.feng.danmaqua.util.ext.avatarUrl
import moe.feng.danmaqua.util.ext.eventsHelper

class VTuberSingleItemViewDelegate :
    ItemBasedSimpleViewBinder<VTuberSingleItem, VTuberSingleItemViewDelegate.ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        get() = viewHolderCreatorOf(R.layout.view_vtubers_single_item)

    class ViewHolder(itemView: View) : ItemBasedViewHolder<VTuberSingleItem>(itemView) {

        override fun onItemClick() {
            context.eventsHelper.of<OnCatalogSingleItemClickListener>().onCatalogSingleItem(data)
        }

        override fun onBind() {
            avatarView.avatarUrl = data.face
            nameText.text = data.name
            descText.text = data.description
        }

    }

}