package moe.feng.danmaqua.ui.settings

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.style.TypefaceSpan
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.google.androidbrowserhelper.trusted.TwaLauncher
import kotlinx.coroutines.launch
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.Danmaqua.ACTION_SETTINGS_UPDATED
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.PreferenceActivity
import moe.feng.danmaqua.ui.dialog.PatternTestDialogFragment
import java.lang.Exception
import java.util.regex.Pattern

class FilterSettingsFragment : BasePreferenceFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.FILTER"

    }

    private val enabledPref by lazy { findPreference<SwitchPreference>("filter_enabled")!! }
    private val patternPref by lazy { findPreference<EditTextPreference>("filter_pattern")!! }
    private val testPatternPref by lazy {
        findPreference<Preference>("filter_test_pattern")!!
    }
    private val blockedUsersPref by lazy {
        findPreference<Preference>("filter_blocked_users")!!
    }
    private val blockedTextPref by lazy {
        findPreference<Preference>("filter_blocked_text")!!
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_filter)

        enabledPref.setOnPreferenceChangeListener(this::onEnabledChanged)

        patternPref.setOnPreferenceChangeListener(this::onPatternChanged)
        patternPref.setSummaryProvider {
            getString(R.string.filter_settings_pattern_summary_format, patternPref.text)
        }

        testPatternPref.setOnPreferenceClickListener {
            PatternTestDialogFragment.newInstance(
                pattern = Settings.Filter.pattern, sampleText = "【测试弹幕】"
            ).show(childFragmentManager, "pattern_test_dialog")
            true
        }

        blockedUsersPref.setOnPreferenceClickListener {
            true
        }

        blockedTextPref.setOnPreferenceClickListener {
            PreferenceActivity.launch(activity!!, ManageBlockedTextFragment.ACTION)
            true
        }

        updatePrefValues()
    }

    private fun updatePrefValues() = launch {
        enabledPref.isChecked = Settings.Filter.enabled
        patternPref.text = Settings.Filter.pattern
        blockedTextPref.summary = getString(R.string.filter_settings_blocked_text_summary_format,
            Settings.Filter.blockedTextPatterns.size)
    }

    override fun onSettingsUpdated() {
        updatePrefValues()
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
            try {
                Pattern.compile(value)
            } catch (e: Exception) {
                e.printStackTrace()
                val msg = buildSpannedString {
                    append(getString(R.string.filter_settings_invalid_pattern_message))
                    append("\n\n")
                    inSpans(TypefaceSpan("monospace")) { append(e.message) }
                }
                activity?.let {
                    AlertDialog.Builder(it)
                        .setTitle(R.string.filter_settings_invalid_pattern_title)
                        .setMessage(msg)
                        .setPositiveButton(android.R.string.ok, null)
                        .setNeutralButton(R.string.action_what_is_regexp) { _, _ ->
                            TwaLauncher(it)
                                .launch(it.getString(R.string.what_is_regexp_tutorial_url).toUri())
                        }
                        .show()
                }
                return false
            }
            Settings.Filter.pattern = value
        }
        Settings.notifyChanged(context!!)
        return true
    }

}