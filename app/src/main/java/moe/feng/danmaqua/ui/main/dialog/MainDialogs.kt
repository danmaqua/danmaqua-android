package moe.feng.danmaqua.ui.main.dialog

import androidx.appcompat.app.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.MainActivity
import moe.feng.danmaqua.ui.MainActivity.Companion.REQUEST_CODE_OVERLAY_PERMISSION
import moe.feng.danmaqua.util.WindowUtils

fun MainActivity.showNoStreamerSelectedDialog() {
    showAlertDialog {
        titleRes = R.string.no_streamer_selected_dialog_title
        messageRes = R.string.no_streamer_selected_dialog_message
        okButton()
    }
}

fun MainActivity.showOverlayPermissionDialog() {
    showAlertDialog {
        titleRes = R.string.overlay_permission_request_title
        messageRes = R.string.overlay_permission_request_message
        positiveButton(R.string.action_allow) {
            launchWhenResumed {
                WindowUtils.requestOverlayPermission(
                    this@showOverlayPermissionDialog, REQUEST_CODE_OVERLAY_PERMISSION)
            }
        }
        negativeButton(R.string.action_deny)
    }
}

fun MainActivity.showConfirmShowFloatingDialog() {
    showAlertDialog {
        titleRes = R.string.ask_show_floating_title
        messageRes = R.string.ask_show_floating_message
        positiveButton(R.string.action_minimize) {
            launchWhenResumed { service.showFloatingWindow() }
            moveTaskToBack(true)
        }
        negativeButton(R.string.action_stay_here) {
            launchWhenResumed { service.showFloatingWindow() }
        }
        neutralButton(android.R.string.cancel)
    }
}

fun MainActivity.showFailedConnectDialog() {
    showAlertDialog {
        titleRes = R.string.failed_to_connect_dialog_title
        messageRes = R.string.failed_to_connect_dialog_message
        okButton()
    }
}
