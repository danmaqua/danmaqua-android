package moe.feng.danmaqua.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.google.androidbrowserhelper.trusted.TwaLauncher
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.settings.dialog.ContactAuthorDialogFragment

class MainSettingsFragment : BasePreferenceFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.MAIN"

    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_main)

        setPreferenceClickListener("user_interface_settings") {
            launchPreference(UserInterfaceSettingsFragment.ACTION)
        }
        setPreferenceClickListener("filter_settings") {
            launchPreference(FilterSettingsFragment.ACTION)
        }
        setPreferenceClickListener("floating_settings") {
            launchPreference(FloatingSettingsFragment.ACTION)
        }
        setPreferenceClickListener("experiment_settings") {
            launchPreference(ExperimentSettingsFragment.ACTION)
        }
        setPreferenceClickListener("about_project_repo") {
            context?.let {
                TwaLauncher(it)
                    .launch(getString(R.string.about_project_repo_url).toUri())
            }
        }
        setPreferenceClickListener("about_contact_author") {
            ContactAuthorDialogFragment()
                .show(parentFragmentManager, "contact_author")
        }
        setPreferenceClickListener("about_discussion_group_qq") {
            context?.let {
                val cm = it.getSystemService<ClipboardManager>()
                cm?.setPrimaryClip(ClipData.newPlainText(
                    "qq", getString(R.string.about_discussion_group_qq_number)))
                Toast.makeText(it, R.string.toast_copied_to_clipboard, Toast.LENGTH_LONG).show()
            }
        }
        setPreferenceClickListener("about_licenses") {
            launchPreference(LicensesFragment.ACTION)
        }
        setPreferenceClickListener("about_road_map") {
            context?.let {
                TwaLauncher(it)
                    .launch(getString(R.string.about_road_map_url).toUri())
            }
        }
    }

}