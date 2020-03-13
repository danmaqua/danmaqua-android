package moe.feng.danmaqua.ui.main.list

import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.databinding.DanmakuSimpleItemViewBinding
import moe.feng.danmaqua.event.MainDanmakuContextMenuListener
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.ui.common.list.*
import androidx.content.eventsHelper

class MainDanmakuItemViewDelegate :
    ItemBasedSimpleViewBinder<BiliChatDanmaku, MainDanmakuItemViewDelegate.ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = dataBindingViewHolderCreatorOf(R.layout.danmaku_simple_item_view)

    class ViewHolder(dataBinding: DanmakuSimpleItemViewBinding) :
        DataBindingViewHolder<BiliChatDanmaku, DanmakuSimpleItemViewBinding>(dataBinding) {

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

    }

}