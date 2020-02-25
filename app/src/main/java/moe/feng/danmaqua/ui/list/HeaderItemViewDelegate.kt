package moe.feng.danmaqua.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewDelegate
import kotlinx.android.synthetic.main.danmaku_history_header_item.*
import moe.feng.danmaqua.R

class HeaderItemViewDelegate : ItemViewDelegate<String, HeaderItemViewDelegate.ViewHolder>() {

    class ViewHolder(itemView: View) : BaseViewHolder(itemView)

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.danmaku_history_header_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: String) {
        holder.title.text = item
    }

}