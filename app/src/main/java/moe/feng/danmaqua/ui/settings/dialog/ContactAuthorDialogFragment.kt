package moe.feng.danmaqua.ui.settings.dialog

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.google.androidbrowserhelper.trusted.TwaLauncher
import kotlinx.android.synthetic.main.contact_author_dialog_layout.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.common.dialog.BaseBottomSheetDialogFragment
import moe.feng.danmaqua.util.IntentUtils
import moe.feng.danmaqua.util.ext.avatarUrl

class ContactAuthorDialogFragment : BaseBottomSheetDialogFragment() {

    override val layoutResourceId: Int = R.layout.contact_author_dialog_layout

    private val email: String get() = getString(R.string.about_author_email_summary)
        .filter { it != '_' }.replace('#', '@')

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        avatarView.avatarUrl = "https://avatars.githubusercontent.com/fython"

        emailSummary.text = email

        weiboButton.setOnClickListener {
            context?.let {
                TwaLauncher(it).launch(getString(R.string.about_author_weibo_url).toUri())
            }
            dismiss()
        }
        bilibiliButton.setOnClickListener {
            context?.let {
                TwaLauncher(it).launch(getString(R.string.about_author_bilibili_url).toUri())
            }
            dismiss()
        }
        emailButton.setOnClickListener {
            context?.let {
                try {
                    it.startActivity(IntentUtils.sendMail(email))
                } catch (e: Exception) {
                    val cm = it.getSystemService<ClipboardManager>()
                    cm?.setPrimaryClip(ClipData.newPlainText("email", email))
                    Toast.makeText(it, R.string.toast_copied_to_clipboard, Toast.LENGTH_LONG).show()
                }
            }
            dismiss()
        }
    }

}