package moe.feng.danmaqua.ui.history

import android.app.Dialog
import android.os.Bundle
import android.text.format.Formatter
import android.widget.TextView
import androidx.core.os.bundleOf
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.dialog.BaseDialogFragment
import moe.feng.danmaqua.util.ext.*
import java.text.DateFormat
import java.util.*

class ViewHistoryItemInfoDialogFragment : BaseDialogFragment() {

    companion object {

        const val ARG_DATA = "args:DATA"

        fun newInstance(data: HistoryItemViewDelegate.Item): ViewHistoryItemInfoDialogFragment {
            return ViewHistoryItemInfoDialogFragment().also {
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
            inflateView(R.layout.history_item_info_dialog) {
                it.findViewById<TextView>(R.id.sourceText).text =
                    "${data.title}(${data.value.roomId})"
                it.findViewById<TextView>(R.id.lastModifiedText).text =
                    DateFormat.getDateTimeInstance()
                        .format(Date().apply { time = data.value.file.lastModified() })
                it.findViewById<TextView>(R.id.fileSizeText).text =
                    Formatter.formatFileSize(it.context, data.value.file.length())
                it.findViewById<TextView>(R.id.filePathText).text =
                    data.value.file.absolutePath
            }
            positiveButton(R.string.action_export) {
                (activity as? ManageHistoryActivity)?.showExportDialog(data.value)
            }
            cancelButton()
            neutralButton(R.string.action_delete) {
                DeleteHistoryItemInfoDialogFragment.newInstance(data)
                    .show(parentFragmentManager, "delete_confirm")
            }
        }
    }

}