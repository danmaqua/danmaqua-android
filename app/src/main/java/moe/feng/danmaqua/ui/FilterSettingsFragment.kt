package moe.feng.danmaqua.ui

import android.content.Context
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.R

class FilterSettingsFragment : BasePreferenceFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.FILTER"

    }

    private val enabledPref by lazy { findPreference<SwitchPreference>("filter_enabled")!! }
    private val patternPref by lazy { findPreference<EditTextPreference>("filter_pattern")!! }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_filter)

        enabledPref.isChecked = Settings.Filter.enabled
        enabledPref.setOnPreferenceChangeListener(this::onEnabledChanged)

        patternPref.text = Settings.Filter.pattern
        patternPref.setOnPreferenceChangeListener(this::onPatternChanged)
        patternPref.setSummaryProvider {
            getString(R.string.filter_settings_pattern_summary_format, patternPref.text)
        }
    }

    override fun getActivityTitle(context: Context): CharSequence? {
        return context.getString(R.string.filter_settings_title)
    }

    private fun onEnabledChanged(pref: Preference, newValue: Any): Boolean {
        val value = newValue as? Boolean ?: false
        Settings.Filter.enabled = value
        Settings.notifyChanged(context!!)
        return true
    }

    private fun onPatternChanged(pref: Preference, newValue: Any): Boolean {
        val value = newValue as? String
        if (value.isNullOrEmpty()) {
            Settings.Filter.pattern = Danmaqua.DEFAULT_FILTER_PATTERN
            patternPref.text = Danmaqua.DEFAULT_FILTER_PATTERN
        } else {
            Settings.Filter.pattern = value
        }
        Settings.notifyChanged(context!!)
        return true
    }

}