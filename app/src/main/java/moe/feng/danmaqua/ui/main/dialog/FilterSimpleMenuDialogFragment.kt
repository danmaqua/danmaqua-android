package moe.feng.danmaqua.ui.main.dialog

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.filter_bottom_sheet_fragment.*
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.R
import moe.feng.danmaqua.data.DanmaquaDB
import moe.feng.danmaqua.event.SettingsChangedListener
import moe.feng.danmaqua.ui.common.dialog.BaseBottomSheetDialogFragment
import moe.feng.danmaqua.ui.settings.FilterSettingsFragment
import moe.feng.danmaqua.ui.settings.PreferenceActivity
import moe.feng.danmaqua.ui.settings.pattern.ManagePatternRulesFragment
import androidx.content.eventsHelper

class FilterSimpleMenuDialogFragment : BaseBottomSheetDialogFragment(), SettingsChangedListener {

    override val layoutResourceId: Int = R.layout.filter_bottom_sheet_fragment

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        updateViewStates()

        filterEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != Danmaqua.Settings.filterEnabled) {
                Danmaqua.Settings.commit { filterEnabled = isChecked }
            }
        }

        patternButton.setOnClickListener {
            PreferenceActivity.launch(requireActivity(), ManagePatternRulesFragment.ACTION)
            dismiss()
        }
        moreSettingsButton.setOnClickListener {
            PreferenceActivity.launch(requireActivity(), FilterSettingsFragment.ACTION)
            dismiss()
        }
        doneFilterButton.setOnClickListener {
            dismiss()
        }

        requireContext().eventsHelper.registerListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().eventsHelper.unregisterListener(this)
    }

    private fun updateViewStates() {
        launchWhenCreated {
            filterEnabledSwitch.isChecked = Danmaqua.Settings.filterEnabled
            filterPatternText.text = DanmaquaDB.instance.patternRules().getSelected().pattern
        }
    }

    override fun onSettingsChanged() {
        updateViewStates()
    }

}