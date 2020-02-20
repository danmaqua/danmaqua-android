package moe.feng.danmaqua.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.FragmentManager
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.NoConnectionsDialogListener
import moe.feng.danmaqua.util.ext.*
import java.lang.Exception

class NoConnectionsDialogFragment : BaseDialogFragment() {

    companion object {

        fun show(fragmentManager: FragmentManager) {
            NoConnectionsDialogFragment().show(fragmentManager, "no_connections")
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return buildAlertDialog {
            titleRes = R.string.no_connections_available_title
            messageRes = R.string.no_connections_available_message
            positiveButton(R.string.action_open_settings) {
                onOpenSettings(context)
            }
            negativeButton(R.string.action_ignore) {
                context.eventsHelper.of<NoConnectionsDialogListener>().onIgnore()
            }
            neutralButton(android.R.string.cancel)
        }
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