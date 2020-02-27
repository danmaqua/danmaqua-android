package moe.feng.danmaqua.ui.history.list

import android.os.Parcelable
import android.text.format.Formatter
import android.view.View
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.danmaku_history_file_item.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.HistoryFile
import moe.feng.danmaqua.ui.list.ItemBasedSimpleViewBinder
import moe.feng.danmaqua.ui.list.ItemBasedViewHolder
import moe.feng.danmaqua.ui.list.viewHolderCreatorOf
import java.text.DateFormat

class HistoryItemViewDelegate(var callback: Callback? = null)
    : ItemBasedSimpleViewBinder<HistoryItemViewDelegate.Item, HistoryItemViewDelegate.ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = viewHolderCreatorOf(R.layout.danmaku_history_file_item)

    interface Callback {

        fun onHistoryItemClick(item: Item)

    }

    @Parcelize
    class Item(val value: HistoryFile, val title: String) : Parcelable

    inner class ViewHolder(itemView: View) : ItemBasedViewHolder<Item>(itemView) {

        override fun onItemClick() {
            callback?.onHistoryItemClick(data)
        }

        override fun onBind() {
            titleText.text = context.getString(
                R.string.danmaku_history_item_title_format, data.title, data.value.roomId)
            descText.text = DateFormat.getDateInstance().format(data.value.getDate().time)
            sizeText.text = Formatter.formatShortFileSize(context, data.value.file.length())
        }

    }

}