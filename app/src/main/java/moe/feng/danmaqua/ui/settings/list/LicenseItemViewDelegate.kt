package moe.feng.danmaqua.ui.settings.list

import android.view.View
import androidx.core.net.toUri
import com.google.androidbrowserhelper.trusted.TwaLauncher
import kotlinx.android.synthetic.main.licenses_item_layout.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.LicenseItem
import moe.feng.danmaqua.ui.list.ItemBasedSimpleViewBinder
import moe.feng.danmaqua.ui.list.ItemBasedViewHolder
import moe.feng.danmaqua.ui.list.viewHolderCreatorOf

class LicenseItemViewDelegate
    : ItemBasedSimpleViewBinder<LicenseItem, LicenseItemViewDelegate.ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = viewHolderCreatorOf(R.layout.licenses_item_layout)

    class ViewHolder(itemView: View) : ItemBasedViewHolder<LicenseItem>(itemView) {

        init {
            cardView.setOnClickListener {
                TwaLauncher(it.context).launch(data.url.toUri())
            }
        }

        override fun onBind() {
            nameText.text = data.productName
            licenseText.text = data.licenseType
            authorText.text = data.author
        }

    }

}