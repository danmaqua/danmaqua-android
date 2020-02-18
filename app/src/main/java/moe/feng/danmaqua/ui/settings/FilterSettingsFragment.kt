package moe.feng.danmaqua.ui.settings

import android.content.Context
import android.os.Bundle
import android.text.style.TypefaceSpan
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.text.buildSpannedString
import androidx.core.text.inSpans
import androidx.lifecycle.lifecycleScope
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.google.androidbrowserhelper.trusted.TwaLauncher
import kotlinx.coroutines.launch
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.R
import moe.feng.danmaqua.data.DanmaquaDB
import moe.feng.danmaqua.ui.PreferenceActivity
import moe.feng.danmaqua.ui.dialog.PatternTestDialogFragment
import moe.feng.danmaqua.util.ext.onClick
import moe.feng.danmaqua.util.ext.onValueChanged
import java.lang.Exception
import java.util.regex.Pattern

class FilterSettingsFragment : BasePreferenceFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.FILTER"

    }

    private val enabledPref by preference<SwitchPreference>("filter_enabled")
    private val patternPref by preference<EditTextPreference>("filter_pattern")
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

        patternPref.onValueChanged { value ->
            onPatternChanged(value)
        }
        patternPref.setSummaryProvider {
            getString(R.string.filter_settings_pattern_summary_format, patternPref.text)
        }

        testPatternPref.onClick {
            PatternTestDialogFragment.newInstance(
                pattern = Settings.filterPattern, sampleText = "【测试弹幕】"
            ).show(childFragmentManager, "pattern_test_dialog")
        }

        blockedUsersPref.onClick {
            PreferenceActivity.launch(activity!!, ManageBlockedUsersFragment.ACTION)
        }

        blockedTextPref.onClick {
            PreferenceActivity.launch(activity!!, ManageBlockedTextFragment.ACTION)
        }

        updatePrefValues()
    }

    private fun updatePrefValues() = lifecycleScope.launch {
        enabledPref.isChecked = Settings.filterEnabled
        patternPref.text = Settings.filterPattern
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

    private fun onPatternChanged(value: String): Boolean {
        if (value.isEmpty()) {
            Settings.filterPattern = Danmaqua.DEFAULT_FILTER_PATTERN
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
            Settings.filterPattern = value
        }
        Settings.notifyChanged(context!!)
        return true
    }

}