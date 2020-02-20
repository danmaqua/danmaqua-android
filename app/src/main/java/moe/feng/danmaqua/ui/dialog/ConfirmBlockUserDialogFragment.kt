package moe.feng.danmaqua.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BlockedUserRule
import moe.feng.danmaqua.ui.view.CircleImageView
import moe.feng.danmaqua.util.ext.*

abstract class ConfirmBlockUserDialogFragment : BaseDialogFragment() {

    companion object {

        const val ARGS_UID = "args:UID"
        const val ARGS_USERNAME = "args:USERNAME"
        const val ARGS_FACE = "args:FACE"

        fun getArguments(uid: Long, username: String, face: String? = null): Bundle {
            return bundleOf(
                ARGS_UID to uid,
                ARGS_USERNAME to username,
                ARGS_FACE to face
            )
        }

    }

    private var uid: Long = 0
    private lateinit var username: String
    private var face: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments!!.let {
            uid = it.getLong(ARGS_UID)
            username = it.getString(ARGS_USERNAME)!!
            face = it.getString(ARGS_FACE)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return buildAlertDialog {
            titleRes = R.string.confirm_add_blocked_user_title
            inflateView(R.layout.manage_blocked_user_confirm_add_dialog_layout) {
                onDialogViewCreated(it, savedInstanceState)
            }
            yesButton {
                lifecycleScope.launch { onConfirmBlock(BlockedUserRule(uid, username, face)) }
            }
            noButton()
        }
    }

    private fun onDialogViewCreated(view: View, savedInstanceState: Bundle?) {
        val avatarView = view.findViewById<CircleImageView>(R.id.avatarView)
        val usernameView = view.findViewById<TextView>(R.id.usernameView)
        val uidView = view.findViewById<TextView>(R.id.uidView)

        avatarView.avatarUrl = face
        usernameView.text = username
        uidView.text = getString(R.string.uid_text_format, uid)
    }

    abstract suspend fun onConfirmBlock(blockRule: BlockedUserRule)

}