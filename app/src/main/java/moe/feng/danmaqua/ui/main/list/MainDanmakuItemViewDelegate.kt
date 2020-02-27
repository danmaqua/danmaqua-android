package moe.feng.danmaqua.ui.main.list

import android.view.View
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.danmaku_simple_item_view.*
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.MainDanmakuContextMenuListener
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.ui.list.ItemBasedSimpleViewBinder
import moe.feng.danmaqua.ui.list.ItemBasedViewHolder
import moe.feng.danmaqua.ui.list.viewHolderCreatorOf
import moe.feng.danmaqua.util.ext.eventsHelper

class MainDanmakuItemViewDelegate :
    ItemBasedSimpleViewBinder<BiliChatDanmaku, MainDanmakuItemViewDelegate.ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = viewHolderCreatorOf(R.layout.danmaku_simple_item_view)

    class ViewHolder(itemView: View) : ItemBasedViewHolder<BiliChatDanmaku>(itemView) {

        init {
            itemView.setOnLongClickListener {
                data.let {
                    context.eventsHelper
                        .of<MainDanmakuContextMenuListener>()
                        .onStartDanmakuContextMenu(it)
                }
                true
            }
        }

        override fun onBind() {
            text1.text = HtmlCompat.fromHtml("<b>${data.senderName}</b> ${data.text}", 0)
        }

    }

}