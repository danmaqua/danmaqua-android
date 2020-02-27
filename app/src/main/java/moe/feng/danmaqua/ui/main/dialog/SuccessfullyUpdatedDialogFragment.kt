package moe.feng.danmaqua.ui.main.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.core.net.toUri
import com.google.androidbrowserhelper.trusted.TwaLauncher
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.dialog.BaseDialogFragment
import moe.feng.danmaqua.util.ext.*

class SuccessfullyUpdatedDialogFragment : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return buildAlertDialog {
            titleRes = R.string.successfully_updated_dialog_title
            message = getString(R.string.successfully_updated_dialog_message,
                context.packageVersionName ?: "Unknown")
            yesButton {
                TwaLauncher(context)
                    .launch(context.getString(R.string.changelog_url).toUri())
            }
            noButton()
        }
    }

}