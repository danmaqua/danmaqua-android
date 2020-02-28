package moe.feng.danmaqua.ui.common.list

import android.view.View
import kotlinx.android.synthetic.main.list_header_item.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.common.list.HeaderItemViewDelegate.ViewHolder

class HeaderItemViewDelegate : ItemBasedSimpleViewBinder<String, ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
            = viewHolderCreatorOf(R.layout.list_header_item)

    class ViewHolder(itemView: View) : ItemBasedViewHolder<String>(itemView) {

        override fun onBind() {
            title.text = data
        }

    }

}