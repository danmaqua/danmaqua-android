package moe.feng.danmaqua.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewDelegate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.view_vtubers_group_item.*
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.OnCatalogGroupItemClickListener
import moe.feng.danmaqua.model.VTuberCatalog
import moe.feng.danmaqua.util.ext.avatarUrl
import moe.feng.danmaqua.util.ext.eventsHelper

class VTuberCatalogItemViewDelegate :
    ItemViewDelegate<VTuberCatalog.Group, VTuberCatalogItemViewDelegate.ViewHolder>() {

    class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        lateinit var data: VTuberCatalog.Group

        init {
            itemView.setOnClickListener {
                it.context.eventsHelper.of<OnCatalogGroupItemClickListener>()
                    .onCatalogItemClick(data)
            }
        }

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.view_vtubers_group_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: VTuberCatalog.Group) {
        with(holder) {
            data = item
            avatarView.avatarUrl = item.icon
            nameText.text = item.title
        }
    }

}