package moe.feng.danmaqua.ui.settings.list

import android.view.View
import androidx.core.net.toUri
import com.google.androidbrowserhelper.trusted.TwaLauncher
import moe.feng.danmaqua.R
import moe.feng.danmaqua.databinding.LicensesItemLayoutBinding
import moe.feng.danmaqua.model.LicenseItem
import moe.feng.danmaqua.ui.list.*

class LicenseItemViewDelegate
    : ItemBasedSimpleViewBinder<LicenseItem, LicenseItemViewDelegate.ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = dataBindingViewHolderCreatorOf(R.layout.licenses_item_layout)

    class ViewHolder(dataBinding: LicensesItemLayoutBinding) :
        DataBindingViewHolder<LicenseItem, LicensesItemLayoutBinding>(dataBinding) {

        fun onCardClick(view: View) {
            TwaLauncher(context).launch(data.url.toUri())
        }

    }

}