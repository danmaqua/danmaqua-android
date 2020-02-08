package moe.feng.danmaqua.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.drakeet.multitype.ItemViewDelegate
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BiliChatDanmaku

class MainDanmakuItemViewDelegate :
    ItemViewDelegate<BiliChatDanmaku, MainDanmakuItemViewDelegate.ViewHolder>() {

    class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        val textView: TextView by bindView(android.R.id.text1)

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.danmaku_simple_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: BiliChatDanmaku) = with(holder) {
        textView.text = HtmlCompat.fromHtml("<b>${item.senderName}</b> ${item.text}", 0)
    }

}