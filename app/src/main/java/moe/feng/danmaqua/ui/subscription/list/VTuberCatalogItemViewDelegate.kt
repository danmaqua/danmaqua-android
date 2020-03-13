package moe.feng.danmaqua.ui.subscription.list

import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.databinding.ViewVtubersGroupItemBinding
import moe.feng.danmaqua.event.OnCatalogGroupItemClickListener
import moe.feng.danmaqua.model.VTuberCatalog
import moe.feng.danmaqua.ui.common.list.*
import androidx.content.eventsHelper

class VTuberCatalogItemViewDelegate :
    ItemBasedSimpleViewBinder<VTuberCatalog.Group, VTuberCatalogItemViewDelegate.ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = dataBindingViewHolderCreatorOf(R.layout.view_vtubers_group_item)

    class ViewHolder(dataBinding: ViewVtubersGroupItemBinding)
        : DataBindingViewHolder<VTuberCatalog.Group, ViewVtubersGroupItemBinding>(dataBinding) {

        override fun onItemClick() {
            context.eventsHelper.of<OnCatalogGroupItemClickListener>().onCatalogItemClick(data)
        }

    }

}