package moe.feng.danmaqua.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import kotlinx.coroutines.launch
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.R
import moe.feng.danmaqua.data.DanmaquaDB
import moe.feng.danmaqua.ui.PreferenceActivity
import moe.feng.danmaqua.ui.dialog.PatternTestDialogFragment
import moe.feng.danmaqua.ui.settings.pattern.ManagePatternRulesFragment
import moe.feng.danmaqua.util.ext.*

class FilterSettingsFragment : BasePreferenceFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.FILTER"

    }

    private val enabledPref by preference<SwitchPreference>("filter_enabled")
    private val patternPref by preference<Preference>("filter_pattern")
    private val testPatternPref by preference<Preference>("filter_test_pattern")
    private val blockedUsersPref by preference<Preference>("filter_blocked_users")
    private val blockedTextPref by preference<Preference>("filter_blocked_text")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_filter)

        enabledPref.onValueChanged { value ->
            Settings.commit {
                filterEnabled = value
            }
            true
        }

        patternPref.onClick {
            PreferenceActivity.launch(requireActivity(), ManagePatternRulesFragment.ACTION)
        }

        testPatternPref.onClick {
            PatternTestDialogFragment.newInstance(
                pattern = Settings.filterPattern, sampleText = "【测试弹幕】"
            ).show(childFragmentManager, "pattern_test_dialog")
        }

        blockedUsersPref.onClick {
            PreferenceActivity.launch(requireActivity(), ManageBlockedUsersFragment.ACTION)
        }

        blockedTextPref.onClick {
            PreferenceActivity.launch(requireActivity(), ManageBlockedTextFragment.ACTION)
        }

        updatePrefValues()
    }

    override fun onResume() {
        super.onResume()
        updatePrefValues()
    }

    private fun updatePrefValues() = lifecycleScope.launch {
        enabledPref.isChecked = Settings.filterEnabled
        patternPref.summary = getString(R.string.filter_settings_pattern_summary_format,
            DanmaquaDB.instance.patternRules().getSelected().pattern)
        blockedUsersPref.summary = getString(R.string.filter_settings_blocked_users_summary_format,
            DanmaquaDB.instance.blockedUsers().count())
        blockedTextPref.summary = getString(R.string.filter_settings_blocked_text_summary_format,
            Settings.blockedTextPatterns.size)
    }

    override fun onSettingsChanged() {
        updatePrefValues()
    }

    override fun getActivityTitle(context: Context): CharSequence? {
        return context.getString(R.string.filter_settings_title)
    }

}