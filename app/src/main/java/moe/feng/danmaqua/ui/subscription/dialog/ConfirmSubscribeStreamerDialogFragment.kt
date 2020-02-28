package moe.feng.danmaqua.ui.subscription.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.OnConfirmSubscribeStreamerListener
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.ui.common.dialog.BaseDialogFragment
import moe.feng.danmaqua.ui.common.view.CircleImageView
import moe.feng.danmaqua.util.ext.*

open class ConfirmSubscribeStreamerDialogFragment : BaseDialogFragment() {

    companion object {

        const val ARGS_DATA = "args:DATA"
        const val ARGS_CALLBACK_TAG = "args:CALLBACK_TAG"

        fun show(fragmentManager: FragmentManager,
                 subscription: Subscription,
                 callbackTag: String? = null) {
            val fragment = ConfirmSubscribeStreamerDialogFragment()
            fragment.arguments = bundleOf(
                ARGS_DATA to subscription, ARGS_CALLBACK_TAG to callbackTag)
            fragment.show(fragmentManager, "confirm_subscribe_streamer")
        }

    }

    protected lateinit var subscription: Subscription
    private var callbackTag: String? = null

    private var onPosClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscription = requireArguments().getParcelable(
            ARGS_DATA
        )!!
        callbackTag = requireArguments().getString(
            ARGS_CALLBACK_TAG
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return buildAlertDialog {
            titleRes = R.string.confirm_subscribe_streamer_dialog_title
            inflateView(R.layout.new_subscription_confirm_dialog_layout) {
                onDialogViewCreated(it, savedInstanceState)
            }
            positiveButton(R.string.action_subscribe) {
                onPositiveButtonClick()
            }
            cancelButton()
        }
    }

    private fun onDialogViewCreated(view: View, savedInstanceState: Bundle?) {
        val avatarView = view.findViewById<CircleImageView>(R.id.avatarView)
        val usernameView = view.findViewById<TextView>(R.id.usernameView)
        val uidView = view.findViewById<TextView>(R.id.uidView)

        avatarView.avatarUrl = subscription.avatar
        usernameView.text = subscription.username
        uidView.text = getString(R.string.room_id_text_format, subscription.roomId)
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (onPosClicked) {
            return
        }
        requireContext().eventsHelper.of<OnConfirmSubscribeStreamerListener>(callbackTag)
            .onCancelConfirmSubscribe()
    }

    open fun onPositiveButtonClick() {
        onPosClicked = true
        requireContext().eventsHelper.of<OnConfirmSubscribeStreamerListener>(callbackTag)
            .onConfirmSubscribeStreamer(subscription)
    }

}