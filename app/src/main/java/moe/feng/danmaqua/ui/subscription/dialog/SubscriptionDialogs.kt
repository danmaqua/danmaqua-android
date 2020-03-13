package moe.feng.danmaqua.ui.subscription.dialog

import androidx.appcompat.app.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.ui.common.BaseActivity
import moe.feng.danmaqua.ui.subscription.NewSubscriptionActivity

fun BaseActivity.showStreamerExistsDialog(username: String) {
    showAlertDialog {
        titleRes = R.string.subscribe_existing_streamer_dialog_title
        message = getString(
            R.string.subscribe_existing_streamer_dialog_message,
            username)
        okButton()
    }
}

fun BaseActivity.showConfirmSubscribeStreamerDialog(subscription: Subscription) {
    ConfirmSubscribeStreamerDialogFragment.show(supportFragmentManager, subscription)
}

fun NewSubscriptionActivity.showSearchNoResultDialog() {
    showAlertDialog {
        titleRes = R.string.search_room_id_no_result_dialog_title
        messageRes = R.string.search_room_id_no_result_dialog_message
        okButton()
    }
}
