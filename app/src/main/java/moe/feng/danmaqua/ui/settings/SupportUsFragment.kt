package moe.feng.danmaqua.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.support_us_fragment_layout.*
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.BaseFragment
import moe.feng.danmaqua.util.IntentUtils
import java.lang.Exception

class SupportUsFragment : BaseFragment() {

    private val alipayEmail: String get() = getString(R.string.alipay_email)
        .filter { it != '_' }.replace('#', '@')

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.SUPPORT_US"

    }

    private var showThanks: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.support_us_fragment_layout, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (showThanks) {
            showThanks = false
            showThanksDialog()
        }
    }

    override fun getActivityTitle(context: Context): String? {
        return context.getString(R.string.support_us_title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        donateViaAlipaySummary.text = alipayEmail
        donateViaAlipayButton.setOnClickListener {
            try {
                startActivity(IntentUtils.openAlipayTransfer())
                showThanks = true
            } catch (e: Exception) {
                AlertDialog.Builder(it.context)
                    .setTitle(R.string.no_alipay_dialog_title)
                    .setMessage(HtmlCompat.fromHtml(
                        getString(R.string.no_alipay_dialog_message, alipayEmail), 0))
                    .setPositiveButton(android.R.string.ok, null)
                    .setNeutralButton(android.R.string.copy) { _, _ ->
                        val cm = it.context.getSystemService<ClipboardManager>()
                        cm?.setPrimaryClip(ClipData.newPlainText("email", alipayEmail))
                        Toast.makeText(it.context,
                            R.string.toast_copied_to_clipboard,
                            Toast.LENGTH_LONG).show()
                        showThanksDialog()
                    }
                    .show()
            }
        }

        donateViaPayPalButton.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.paypal_link).toUri()))
                showThanks = true
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun showThanksDialog() {
        context?.let {
            AlertDialog.Builder(it)
                .setTitle(R.string.thank_you_dialog_title)
                .setMessage(R.string.thank_you_dialog_message)
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }
    }

}