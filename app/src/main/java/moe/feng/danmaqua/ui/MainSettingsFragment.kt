package moe.feng.danmaqua.ui

import android.os.Bundle
import androidx.preference.Preference
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.R

class MainSettingsFragment : BasePreferenceFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.MAIN"

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_main)

        findPreference<Preference>("filter_settings")!!.setOnPreferenceClickListener {
            PreferenceActivity.launch(activity!!, FilterSettingsFragment.ACTION)
            true
        }
        findPreference<Preference>("floating_settings")!!.setOnPreferenceClickListener {
            PreferenceActivity.launch(activity!!, FloatingSettingsFragment.ACTION)
            true
        }
    }

}