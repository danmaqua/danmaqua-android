package moe.feng.danmaqua.ui.subscription.list

import android.view.View
import kotlinx.android.synthetic.main.view_vtubers_group_item.*
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.OnCatalogGroupItemClickListener
import moe.feng.danmaqua.model.VTuberCatalog
import moe.feng.danmaqua.ui.list.ItemBasedSimpleViewBinder
import moe.feng.danmaqua.ui.list.ItemBasedViewHolder
import moe.feng.danmaqua.ui.list.viewHolderCreatorOf
import moe.feng.danmaqua.util.ext.avatarUrl
import moe.feng.danmaqua.util.ext.eventsHelper

class VTuberCatalogItemViewDelegate :
    ItemBasedSimpleViewBinder<VTuberCatalog.Group, VTuberCatalogItemViewDelegate.ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = viewHolderCreatorOf(R.layout.view_vtubers_group_item)

    class ViewHolder(itemView: View) : ItemBasedViewHolder<VTuberCatalog.Group>(itemView) {

        override fun onItemClick() {
            context.eventsHelper.of<OnCatalogGroupItemClickListener>().onCatalogItemClick(data)
        }

        override fun onBind() {
            avatarView.avatarUrl = data.icon
            nameText.text = data.title
        }

    }

}