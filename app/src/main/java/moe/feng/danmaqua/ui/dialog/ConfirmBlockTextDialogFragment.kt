package moe.feng.danmaqua.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BlockedTextRule

abstract class ConfirmBlockTextDialogFragment : BaseDialogFragment() {

    companion object {

        const val ARGS_INITIAL_VALUE = "args:INITIAL_VALUE"

        const val STATE_DATA = "state:DATA"

    }

    private lateinit var data: BlockedTextRule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            data = arguments?.getParcelable(ARGS_INITIAL_VALUE) ?: BlockedTextRule("")
        } else {
            data = savedInstanceState.getParcelable(STATE_DATA) ?: BlockedTextRule("")
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(STATE_DATA, data)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.manage_blocked_text_edit_dialog_layout, null)
        val editText = dialogView.findViewById<EditText>(android.R.id.edit)
        val checkboxRegexp = dialogView.findViewById<CheckBox>(R.id.checkboxRegexp)

        editText.setText(data.text)
        checkboxRegexp.isChecked = data.isRegExp

        editText.addTextChangedListener {
            it?.toString()?.let { str ->
                data.text = str
            }
        }
        checkboxRegexp.setOnCheckedChangeListener { _, isChecked ->
            data.isRegExp = isChecked
        }

        return AlertDialog.Builder(activity!!)
            .setTitle(titleRes)
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                if (data.text.isEmpty()) {
                    Toast.makeText(context!!, R.string.toast_empty_input, Toast.LENGTH_SHORT)
                        .show()
                    return@setPositiveButton
                }
                onBlockText(BlockedTextRule(data.text, data.isRegExp))
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    abstract val titleRes: Int

    abstract fun onBlockText(rule: BlockedTextRule)

}