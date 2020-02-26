package moe.feng.danmaqua.ui.history

import android.content.Context
import android.os.Parcelable
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drakeet.multitype.ItemViewDelegate
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.danmaku_history_file_item.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.HistoryFile
import moe.feng.danmaqua.ui.list.BaseViewHolder
import java.text.DateFormat

class HistoryItemViewDelegate(var callback: Callback? = null)
    : ItemViewDelegate<HistoryItemViewDelegate.Item, HistoryItemViewDelegate.ViewHolder>() {

    interface Callback {

        fun onHistoryItemClick(item: Item)

    }

    @Parcelize
    class Item(
        val value: HistoryFile,
        val title: String
    ) : Parcelable

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        lateinit var data: Item

        override fun onItemClick() {
            callback?.onHistoryItemClick(data)
        }

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.danmaku_history_file_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Item) {
        with(holder) {
            data = item
            titleText.text = context.getString(
                R.string.danmaku_history_item_title_format, item.title, item.value.roomId)
            descText.text = DateFormat.getDateInstance().format(item.value.getDate().time)
            sizeText.text = Formatter.formatShortFileSize(context, item.value.file.length())
        }
    }

}