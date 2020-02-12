package moe.feng.danmaqua.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.NoConnectionsDialogListener
import moe.feng.danmaqua.util.ext.eventsHelper
import java.lang.Exception

class NoConnectionsDialogFragment : BaseDialogFragment() {

    companion object {

        fun show(fragmentManager: FragmentManager) {
            NoConnectionsDialogFragment().show(fragmentManager, "no_connections")
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(context!!)
            .setTitle(R.string.no_connections_available_title)
            .setMessage(R.string.no_connections_available_message)
            .setPositiveButton(R.string.action_open_settings) { _, _ ->
                context?.run(::onOpenSettings)
            }
            .setNegativeButton(R.string.action_ignore) { _, _ ->
                context?.eventsHelper?.of<NoConnectionsDialogListener>()
                    ?.onIgnore()
            }
            .setNeutralButton(android.R.string.cancel, null)
            .create()
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