package moe.feng.danmaqua.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewDelegate
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.view_vtubers_single_item.*
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.OnCatalogSingleItemClickListener
import moe.feng.danmaqua.model.VTuberSingleItem
import moe.feng.danmaqua.util.ext.avatarUrl
import moe.feng.danmaqua.util.ext.eventsHelper

class VTuberSingleItemViewDelegate :
    ItemViewDelegate<VTuberSingleItem, VTuberSingleItemViewDelegate.ViewHolder>() {

    class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        lateinit var data: VTuberSingleItem

        init {
            itemView.setOnClickListener {
                it.context.eventsHelper.of<OnCatalogSingleItemClickListener>()
                    .onCatalogSingleItem(data)
            }
        }

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.view_vtubers_single_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: VTuberSingleItem) {
        with(holder) {
            data = item
            avatarView.avatarUrl = item.face
            nameText.text = item.name
            descText.text = item.description
        }
    }

}