package moe.feng.danmaqua.ui.settings.pattern

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.core.os.bundleOf
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.PatternRulesItem
import moe.feng.danmaqua.ui.dialog.BaseDialogFragment
import moe.feng.danmaqua.util.ext.buildAlertDialog
import moe.feng.danmaqua.util.ext.inflateView
import moe.feng.danmaqua.util.ext.okButton

class ViewPatternRuleDialogFragment : BaseDialogFragment() {

    companion object {

        const val ARG_DATA = "args:DATA"

        fun newInstance(data: PatternRulesItem): ViewPatternRuleDialogFragment {
            return ViewPatternRuleDialogFragment().also {
                it.arguments = bundleOf(ARG_DATA to data)
            }
        }

    }

    private lateinit var data: PatternRulesItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        data = requireArguments().getParcelable(ARG_DATA)!!
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return buildAlertDialog {
            inflateView(R.layout.manage_pattern_rules_info_dialog) {
                it.findViewById<TextView>(R.id.titleText).text = data.title()
                it.findViewById<TextView>(R.id.descText).text = data.desc()
                it.findViewById<TextView>(R.id.committerText).text = data.committer
                it.findViewById<TextView>(R.id.regexpText).text = data.pattern
            }
            okButton()
        }
    }

}