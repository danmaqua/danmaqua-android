package moe.feng.danmaqua.ui.main.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.*
import androidx.content.launchViewUrl
import androidx.content.packageVersionName
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.common.dialog.BaseDialogFragment

class SuccessfullyUpdatedDialogFragment : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return buildAlertDialog {
            titleRes = R.string.successfully_updated_dialog_title
            message = getString(R.string.successfully_updated_dialog_message,
                context.packageVersionName ?: "Unknown")
            yesButton {
                context.launchViewUrl(R.string.changelog_url)
            }
            noButton()
        }
    }

}