package moe.feng.danmaqua.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import com.google.androidbrowserhelper.trusted.TwaLauncher
import moe.feng.danmaqua.R
import moe.feng.danmaqua.util.ext.packageVersionName

class SuccessfullyUpdatedDialogFragment : BaseDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(activity!!)
            .setTitle(R.string.successfully_updated_dialog_title)
            .setMessage(getString(R.string.successfully_updated_dialog_message,
                context?.packageVersionName ?: "Unknown"))
            .setPositiveButton(android.R.string.yes) { _, _ ->
                context?.let {
                    TwaLauncher(it)
                        .launch(it.getString(R.string.changelog_url).toUri())
                }
            }
            .setNegativeButton(android.R.string.no, null)
            .create()
    }

}