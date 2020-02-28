package moe.feng.danmaqua.ui.floating.list

import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.updateLayoutParams
import kotlinx.android.synthetic.main.danmaku_fw_item_view.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.ui.floating.FloatingWindowHolder
import moe.feng.danmaqua.ui.common.list.ItemBasedSimpleViewBinder
import moe.feng.danmaqua.ui.common.list.ItemBasedViewHolder
import moe.feng.danmaqua.ui.common.list.viewHolderCreatorOf
import moe.feng.danmaqua.util.flattenToString

class FWDanmakuItemViewDelegate(val fwHolder: FloatingWindowHolder) :
    ItemBasedSimpleViewBinder<BiliChatDanmaku, FWDanmakuItemViewDelegate.ViewHolder>(){

    override val viewHolderCreator: ViewHolderCreator<ViewHolder> =
        viewHolderCreatorOf(R.layout.danmaku_fw_item_view)

    inner class ViewHolder(itemView: View) : ItemBasedViewHolder<BiliChatDanmaku>(itemView) {

        override fun onBind() {
            val itemText = fwHolder.danmakuFilter.unescapeSubtitle(data)?.flattenToString()
            if (itemText != null) {
                text1.textSize = fwHolder.textSize.toFloat()
                val latest = adapterPosition + 1 == fwHolder.listView.layoutManager?.itemCount
                text1.text = if (latest) {
                    buildSpannedString {
                        bold { append(itemText) }
                    }
                } else {
                    itemText
                }
                itemView.updateLayoutParams {
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            } else {
                itemView.updateLayoutParams {
                    height = 0
                }
            }
        }

    }

}