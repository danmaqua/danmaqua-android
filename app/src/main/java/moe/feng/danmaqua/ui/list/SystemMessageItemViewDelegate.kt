package moe.feng.danmaqua.ui.list

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.text.HtmlCompat
import com.drakeet.multitype.ItemViewDelegate
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.list.SystemMessageItemViewDelegate.*

class SystemMessageItemViewDelegate : ItemViewDelegate<Item, ViewHolder>() {

    data class Item(val text: String) : Parcelable {

        constructor(parcel: Parcel) : this(parcel.readString()!!)

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(text)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Item> {
            override fun createFromParcel(parcel: Parcel): Item {
                return Item(parcel)
            }

            override fun newArray(size: Int): Array<Item?> {
                return arrayOfNulls(size)
            }
        }

    }

    class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        val textView: TextView by bindView(android.R.id.text1)

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.danmaku_simple_item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: Item) {
        holder.textView.text = HtmlCompat.fromHtml(item.text, 0)
    }

}