package moe.feng.danmaqua.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.DropDownPreference
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.R
import moe.feng.danmaqua.util.IntentUtils
import androidx.preference.onValueChanged

class UserInterfaceSettingsFragment : BasePreferenceFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.UI"

    }

    private val darkMode by preference<DropDownPreference>("dark_mode")

    override fun getActivityTitle(context: Context): CharSequence? {
        return context.getString(R.string.user_interface_title)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_ui)

        var currentDarkMode = AppCompatDelegate.getDefaultNightMode().toString()
        if (currentDarkMode !in resources.getStringArray(R.array.dark_mode_entry_values)) {
            currentDarkMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM.toString()
        }
        darkMode.value = currentDarkMode
        darkMode.onValueChanged { newValue ->
            val value = newValue.toInt()
            AppCompatDelegate.setDefaultNightMode(value)
            Danmaqua.Settings.darkMode = value
            activity?.let { IntentUtils.restartApp(it) }
            true
        }
    }

}