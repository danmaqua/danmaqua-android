package moe.feng.danmaqua.ui

import android.content.Context
import android.os.Bundle
import androidx.preference.EditTextPreference
import androidx.preference.Preference
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
        findPreference<EditTextPreference>("floating_background_alpha")!!
    }
    private val textSizePref by lazy {
        findPreference<EditTextPreference>("floating_text_size")!!
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_floating)

        twoLinePref.isChecked = Settings.Floating.twoLine
        twoLinePref.setOnPreferenceChangeListener(this::onTwoLineChanged)

        backgroundAlphaPref.text = Settings.Floating.backgroundAlpha.toString()

        textSizePref.text = Settings.Floating.textSize.toString()
    }

    override fun getActivityTitle(context: Context): CharSequence? {
        return context.getString(R.string.floating_settings_title)
    }

    private fun onTwoLineChanged(pref: Preference, newValue: Any): Boolean {
        val newBool = newValue as Boolean
        Settings.Floating.twoLine = newBool
        return true
    }

}