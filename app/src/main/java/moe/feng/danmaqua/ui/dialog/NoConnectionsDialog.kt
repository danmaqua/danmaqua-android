package moe.feng.danmaqua.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import moe.feng.danmaqua.R
import java.lang.Exception

object NoConnectionsDialog {

    fun show(context: Context, onIgnored: () -> Unit): Dialog {
        return AlertDialog.Builder(context)
            .setTitle(R.string.no_connections_available_title)
            .setMessage(R.string.no_connections_available_message)
            .setPositiveButton(R.string.action_open_settings) { _, _ -> onOpenSettings(context) }
            .setNegativeButton(R.string.action_ignore) { _, _ -> onIgnored() }
            .setNeutralButton(android.R.string.cancel, null)
            .show()
    }

    private fun onOpenSettings(context: Context) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
        } else {
            Intent(Settings.ACTION_WIFI_SETTINGS)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}