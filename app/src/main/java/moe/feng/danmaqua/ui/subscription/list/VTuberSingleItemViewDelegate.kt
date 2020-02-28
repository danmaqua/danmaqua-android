package moe.feng.danmaqua.ui.subscription.list

import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.databinding.ViewVtubersSingleItemBinding
import moe.feng.danmaqua.event.OnCatalogSingleItemClickListener
import moe.feng.danmaqua.model.VTuberSingleItem
import moe.feng.danmaqua.ui.common.list.*
import moe.feng.danmaqua.util.ext.eventsHelper

class VTuberSingleItemViewDelegate :
    ItemBasedSimpleViewBinder<VTuberSingleItem, VTuberSingleItemViewDelegate.ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = dataBindingViewHolderCreatorOf(R.layout.view_vtubers_single_item)

    class ViewHolder(dataBinding: ViewVtubersSingleItemBinding)
        : DataBindingViewHolder<VTuberSingleItem, ViewVtubersSingleItemBinding>(dataBinding) {

        override fun onItemClick() {
            context.eventsHelper.of<OnCatalogSingleItemClickListener>().onCatalogSingleItem(data)
        }

    }

}