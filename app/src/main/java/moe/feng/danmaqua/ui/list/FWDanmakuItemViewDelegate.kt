package moe.feng.danmaqua.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.updateLayoutParams
import com.drakeet.multitype.ItemViewDelegate
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.ui.floating.FloatingWindowHolder
import moe.feng.danmaqua.util.flattenToString

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
        val itemText = fwHolder.danmakuFilter.unescapeSubtitle(item)?.flattenToString()
        with (holder) {
            if (itemText != null) {
                textView.textSize = fwHolder.textSize.toFloat()
                val latest = holder.adapterPosition + 1 ==
                        fwHolder.listView.layoutManager?.itemCount
                textView.text = if (latest) {
                    buildSpannedString {
                        bold { append(itemText) }
                    }
                } else {
                    itemText
                }
                holder.itemView.updateLayoutParams {
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            } else {
                holder.itemView.updateLayoutParams {
                    height = 0
                }
            }
        }
    }

}