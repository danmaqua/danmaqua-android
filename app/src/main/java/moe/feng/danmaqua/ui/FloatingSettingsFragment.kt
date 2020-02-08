package moe.feng.danmaqua.ui

import android.content.Context
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.R

class FloatingSettingsFragment : BasePreferenceFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.FLOATING"

    }

    private val twoLinePref by lazy {
        findPreference<SwitchPreference>("floating_two_line")!!
    }
    private val backgroundAlphaPref by lazy {
        findPreference<SeekBarPreference>("floating_background_alpha")!!
    }
    private val textSizePref by lazy {
        findPreference<SeekBarPreference>("floating_text_size")!!
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_floating)

        twoLinePref.isChecked = Settings.Floating.twoLine
        twoLinePref.setOnPreferenceChangeListener(this::onTwoLineChanged)

        backgroundAlphaPref.value = Settings.Floating.backgroundAlpha
        backgroundAlphaPref.setOnPreferenceChangeListener(this::onBackgroundAlphaChanged)
        backgroundAlphaPref.setSummaryProvider {
            getString(R.string.floating_background_alpha_summary, backgroundAlphaPref.value)
        }

        textSizePref.value = Settings.Floating.textSize
        textSizePref.setOnPreferenceChangeListener(this::onTextSizeChanged)
        textSizePref.setSummaryProvider {
            getString(R.string.floating_text_size_summary, textSizePref.value)
        }
    }

    override fun getActivityTitle(context: Context): CharSequence? {
        return context.getString(R.string.floating_settings_title)
    }

    private fun onTwoLineChanged(pref: Preference, newValue: Any): Boolean {
        val newBool = newValue as Boolean
        Settings.Floating.twoLine = newBool
        Settings.notifyChanged(context!!)
        return true
    }

    private fun onBackgroundAlphaChanged(pref: Preference, newValue: Any): Boolean {
        val newInt = newValue as Int
        Settings.Floating.backgroundAlpha = newInt.coerceIn(100..255)
        Settings.notifyChanged(context!!)
        return true
    }

    private fun onTextSizeChanged(pref: Preference, newValue: Any): Boolean {
        val newInt = newValue as Int
        Settings.Floating.textSize = newInt.coerceIn(10..30)
        Settings.notifyChanged(context!!)
        return true
    }

}