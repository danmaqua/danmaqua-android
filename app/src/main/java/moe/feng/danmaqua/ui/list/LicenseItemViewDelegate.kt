package moe.feng.danmaqua.ui.list

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.drakeet.multitype.ItemViewDelegate
import com.google.androidbrowserhelper.trusted.TwaLauncher
import kotlinx.android.synthetic.main.licenses_item_layout.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.LicenseItem

class LicenseItemViewDelegate
    : ItemViewDelegate<LicenseItem, LicenseItemViewDelegate.ViewHolder>() {

    class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

        lateinit var data: LicenseItem

        init {
            cardView.setOnClickListener {
                TwaLauncher(it.context).launch(data.url.toUri())
            }
        }

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.licenses_item_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: LicenseItem) {
        with(holder) {
            nameText.text = item.productName
            licenseText.text = item.licenseType
            authorText.text = item.author
            data = item
        }
    }

}