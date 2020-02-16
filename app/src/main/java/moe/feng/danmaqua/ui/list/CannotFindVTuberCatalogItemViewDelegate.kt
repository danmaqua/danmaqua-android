package moe.feng.danmaqua.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.drakeet.multitype.ItemViewDelegate
import com.google.androidbrowserhelper.trusted.TwaLauncher
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.list.CannotFindVTuberCatalogItemViewDelegate.Item
import moe.feng.danmaqua.ui.list.CannotFindVTuberCatalogItemViewDelegate.ViewHolder

class CannotFindVTuberCatalogItemViewDelegate :
    ItemViewDelegate<Item, ViewHolder>() {

    object Item

    class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                val uri = it.context.getString(R.string.cannot_find_vtuber_in_catalog_url).toUri()
                TwaLauncher(it.context).launch(uri)
            }
        }

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.cannot_find_vtuber_catalog_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Item) {

    }

}