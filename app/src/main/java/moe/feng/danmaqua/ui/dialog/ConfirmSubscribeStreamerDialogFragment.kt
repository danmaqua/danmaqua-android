package moe.feng.danmaqua.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.OnConfirmSubscribeStreamerListener
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.ui.view.CircleImageView
import moe.feng.danmaqua.util.ext.*

open class ConfirmSubscribeStreamerDialogFragment : BaseDialogFragment() {

    companion object {

        const val ARGS_DATA = "args:DATA"

        fun show(fragmentManager: FragmentManager, subscription: Subscription) {
            val fragment = ConfirmSubscribeStreamerDialogFragment()
            fragment.arguments = bundleOf(ARGS_DATA to subscription)
            fragment.show(fragmentManager, "confirm_subscribe_streamer")
        }

    }

    protected lateinit var subscription: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        subscription = arguments!!.getParcelable(ARGS_DATA)!!
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

    open fun onPositiveButtonClick() {
        context!!.eventsHelper.of<OnConfirmSubscribeStreamerListener>()
            .onConfirmSubscribeStreamer(subscription)
    }

}