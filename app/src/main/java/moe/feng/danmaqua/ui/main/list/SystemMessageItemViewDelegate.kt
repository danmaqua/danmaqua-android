package moe.feng.danmaqua.ui.main.list

import android.os.Parcel
import android.os.Parcelable
import android.view.View
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.danmaku_simple_item_view.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.common.list.ItemBasedSimpleViewBinder
import moe.feng.danmaqua.ui.common.list.ItemBasedViewHolder
import moe.feng.danmaqua.ui.common.list.viewHolderCreatorOf
import moe.feng.danmaqua.ui.main.list.SystemMessageItemViewDelegate.Item
import moe.feng.danmaqua.ui.main.list.SystemMessageItemViewDelegate.ViewHolder

class SystemMessageItemViewDelegate : ItemBasedSimpleViewBinder<Item, ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = viewHolderCreatorOf(R.layout.danmaku_simple_item_view)

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

    class ViewHolder(itemView: View) : ItemBasedViewHolder<Item>(itemView) {

        override fun onBind() {
            text1.text = HtmlCompat.fromHtml(data.text, 0)
        }

    }

}