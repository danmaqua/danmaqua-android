package moe.feng.danmaqua.ui.history

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.*
import androidx.core.os.bundleOf
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.common.dialog.BaseDialogFragment
import moe.feng.danmaqua.ui.history.list.HistoryItemViewDelegate

class DeleteHistoryItemInfoDialogFragment : BaseDialogFragment() {

    companion object {

        const val ARG_DATA = "args:DATA"

        fun newInstance(data: HistoryItemViewDelegate.Item): DeleteHistoryItemInfoDialogFragment {
            return DeleteHistoryItemInfoDialogFragment().also {
                it.arguments = bundleOf(ARG_DATA to data)
            }
        }

    }

    private lateinit var data: HistoryItemViewDelegate.Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        data = requireArguments().getParcelable(ARG_DATA)!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return buildAlertDialog {
            titleRes = R.string.danmaku_history_delete_confirm_title
            message = getString(R.string.danmaku_history_delete_confirm_message,
                data.value.file.absolutePath)
            okButton { (activity as? ManageHistoryActivity)?.onConfirmDelete(data.value) }
            cancelButton()
        }
    }

}