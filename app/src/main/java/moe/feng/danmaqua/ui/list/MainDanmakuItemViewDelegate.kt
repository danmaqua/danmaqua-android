package moe.feng.danmaqua.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import com.drakeet.multitype.ItemViewDelegate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.danmaku_simple_item_view.*
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.MainDanmakuContextMenuListener
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.util.ext.eventsHelper

class MainDanmakuItemViewDelegate :
    ItemViewDelegate<BiliChatDanmaku, MainDanmakuItemViewDelegate.ViewHolder>() {

    class ViewHolder(override val containerView: View)
        : BaseViewHolder(containerView), LayoutContainer {

        var data: BiliChatDanmaku? = null

        init {
            containerView.setOnLongClickListener {
                data?.let {
                    containerView.context.eventsHelper
                        .of<MainDanmakuContextMenuListener>()
                        .onStartDanmakuContextMenu(it)
                }
                true
            }
        }

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.danmaku_simple_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: BiliChatDanmaku) = with(holder) {
        data = item
        text1.text = HtmlCompat.fromHtml("<b>${item.senderName}</b> ${item.text}", 0)
    }

}