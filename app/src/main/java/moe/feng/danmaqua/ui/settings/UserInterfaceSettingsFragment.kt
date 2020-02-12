package moe.feng.danmaqua.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.DropDownPreference
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.R

class UserInterfaceSettingsFragment : BasePreferenceFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.UI"

    }

    private val darkMode by preference<DropDownPreference>("dark_mode")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_ui)

        var currentDarkMode = AppCompatDelegate.getDefaultNightMode().toString()
        if (currentDarkMode !in resources.getStringArray(R.array.dark_mode_entry_values)) {
            currentDarkMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()
        }
        darkMode.value = currentDarkMode
        darkMode.setOnPreferenceChangeListener { _, newValue ->
            AppCompatDelegate.setDefaultNightMode((newValue as String).toInt())
            true
        }
    }

}