package moe.feng.danmaqua.ui.settings.pattern

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.*
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.code.regexp.Pattern
import kotlinx.coroutines.Job
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.PatternRulesItem
import moe.feng.danmaqua.model.buildTextTranslation
import moe.feng.danmaqua.model.chinese
import moe.feng.danmaqua.ui.common.dialog.BaseDialogFragment
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
    private lateinit var titleInputLayout: TextInputLayout
    private lateinit var regexpInputLayout: TextInputLayout

    private var okButton: Button? = null
    private var updateViewStateJob: Job? = null

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
                titleInputLayout = it.findViewById(R.id.titleInputLayout)
                regexpInputLayout = it.findViewById(R.id.regexpInputLayout)

                titleEdit.setText(data.title())
                regexpEdit.setText(data.pattern)

                titleEdit.addTextChangedListener { text ->
                    data.title = buildTextTranslation {
                        chinese = text.toString()
                    }
                    updateViewState()
                }
                regexpEdit.addTextChangedListener { text ->
                    data.pattern = text.toString()
                    updateViewState()
                }
            }
            okButton {
                val parent = parentFragment
                if (parent is ManagePatternRulesFragment) {
                    when (action) {
                        ACTION_NEW -> parent.onConfirmRuleAdd(data)
                        ACTION_EDIT -> parent.onConfirmRuleEdit(data)
                    }
                }
            }
            cancelButton()
        }.also {
            it.setOnShowListener { _ ->
                okButton = it.positiveButton
                updateViewState()
            }
        }
    }

    private fun updateViewState() {
        updateViewStateJob?.cancel()
        updateViewStateJob = launchWhenCreated {
            val titleEmpty = titleEdit.text.isNullOrEmpty()
            val regexpInvalid = try {
                Pattern.compile(data.pattern)
                false
            } catch (e: Exception) {
                true
            }
            val regexpEmpty = regexpEdit.text.isNullOrEmpty()

            titleInputLayout.error = when {
                titleEmpty -> getString(R.string.pattern_rule_title_error_empty)
                else -> null
            }
            regexpInputLayout.error = when {
                regexpInvalid -> getString(R.string.pattern_rule_regex_error_invalid)
                regexpEmpty -> getString(R.string.pattern_rule_regex_error_empty)
                else -> null
            }
            okButton?.isEnabled = !titleEmpty && !regexpInvalid && !regexpEmpty
        }
    }

}