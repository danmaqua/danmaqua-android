package moe.feng.danmaqua.ui.settings.pattern

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.code.regexp.Pattern
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.PatternRulesItem
import moe.feng.danmaqua.model.buildTextTranslation
import moe.feng.danmaqua.model.chinese
import moe.feng.danmaqua.ui.dialog.BaseDialogFragment
import moe.feng.danmaqua.util.ext.*
import java.util.*

class EditPatternRuleDialogFragment : BaseDialogFragment() {

    companion object {

        const val ACTION_NEW = 1
        const val ACTION_EDIT = 2

        const val ARG_ACTION = "args:ACTION"
        const val ARG_DATA = "args:DATA"

        fun showNewDialog(): EditPatternRuleDialogFragment {
            return EditPatternRuleDialogFragment().also {
                it.arguments = bundleOf(ARG_ACTION to ACTION_NEW)
            }
        }

        fun showEditDialog(item: PatternRulesItem): EditPatternRuleDialogFragment {
            if (!item.local) {
                throw IllegalArgumentException("Cannot edit online rule")
            }
            return EditPatternRuleDialogFragment().also {
                it.arguments = bundleOf(
                    ARG_ACTION to ACTION_EDIT,
                    ARG_DATA to PatternRulesItem(
                        id = item.id,
                        title = item.title,
                        pattern = item.pattern,
                        selected = item.selected,
                        local = true
                    )
                )
            }
        }

    }

    private var action: Int = 0
    private lateinit var data: PatternRulesItem

    private lateinit var titleEdit: TextInputEditText
    private lateinit var regexpEdit: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        action = requireArguments().getInt(ARG_ACTION)
        if (savedInstanceState == null) {
            if (action == ACTION_EDIT) {
                data = requireArguments().getParcelable(ARG_DATA)!!
            } else {
                data = PatternRulesItem(id = "local:" + UUID.randomUUID().toString())
            }
        } else {
            data = savedInstanceState.getParcelable(ARG_DATA)!!
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(ARG_DATA, data)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return buildAlertDialog {
            titleRes = when (action) {
                ACTION_NEW -> R.string.pattern_rule_add_dialog_title_add
                ACTION_EDIT -> R.string.pattern_rule_add_dialog_title_edit
                else -> throw IllegalArgumentException()
            }
            inflateView(R.layout.manage_pattern_rules_add_dialog) {
                titleEdit = it.findViewById(R.id.titleEdit)
                regexpEdit = it.findViewById(R.id.regexpEdit)

                titleEdit.setText(data.title())
                regexpEdit.setText(data.pattern)

                titleEdit.addTextChangedListener { text ->
                    data.title = buildTextTranslation {
                        chinese = text.toString()
                    }
                }
                regexpEdit.addTextChangedListener { text ->
                    data.pattern = text.toString()
                }
            }
            okButton {
                if (titleEdit.text.isNullOrEmpty()) {
                    // TODO Show toast
                    return@okButton
                }
                try {
                    Pattern.compile(regexpEdit.text.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        R.string.filter_settings_invalid_pattern_title,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val parent = parentFragment
                if (parent is ManagePatternRulesFragment) {
                    when (action) {
                        ACTION_NEW -> parent.onConfirmRuleAdd(data)
                        ACTION_EDIT -> parent.onConfirmRuleEdit(data)
                    }
                }
            }
            cancelButton()
        }
    }

}