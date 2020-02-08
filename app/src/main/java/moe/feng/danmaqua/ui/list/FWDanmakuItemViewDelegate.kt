package moe.feng.danmaqua.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import com.drakeet.multitype.ItemViewDelegate
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.ui.floating.FloatingWindowHolder

class FWDanmakuItemViewDelegate(val fwHolder: FloatingWindowHolder) :
    ItemViewDelegate<BiliChatDanmaku, FWDanmakuItemViewDelegate.ViewHolder>(){

    class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        val textView: TextView by bindView(android.R.id.text1)

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.danmaku_fw_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: BiliChatDanmaku) {
        val itemText = fwHolder.danmakuFilter.unescapeCaption(item)
        with (holder) {
            textView.textSize = fwHolder.textSize.toFloat()
            val latest = holder.adapterPosition + 1 == fwHolder.listView.layoutManager?.itemCount
            textView.text = if (latest) {
                buildSpannedString {
                    bold { append(itemText) }
                }
            } else {
                itemText
            }
        }
    }

}