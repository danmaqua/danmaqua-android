package moe.feng.danmaqua.ui.settings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import kotlinx.android.synthetic.main.preference_activity.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.common.BaseActivity
import moe.feng.danmaqua.ui.settings.pattern.ManagePatternRulesFragment
import kotlinx.TAG

class PreferenceActivity : BaseActivity() {

    companion object {

        fun launch(activity: Activity, action: String) {
            val intent = Intent(activity, PreferenceActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            intent.action = action
            activity.startActivity(intent)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preference_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            when (val action = intent?.action) {
                MainSettingsFragment.ACTION -> {
                    setFragment(
                        MainSettingsFragment()
                    )
                }
                UserInterfaceSettingsFragment.ACTION -> {
                    setFragment(
                        UserInterfaceSettingsFragment()
                    )
                }
                FilterSettingsFragment.ACTION -> {
                    setFragment(
                        FilterSettingsFragment()
                    )
                }
                FloatingSettingsFragment.ACTION -> {
                    setFragment(
                        FloatingSettingsFragment()
                    )
                }
                ManagePatternRulesFragment.ACTION -> {
                    setFragment(
                        ManagePatternRulesFragment()
                    )
                }
                ManageBlockedTextFragment.ACTION -> {
                    setFragment(
                        ManageBlockedTextFragment()
                    )
                }
                ManageBlockedUsersFragment.ACTION -> {
                    setFragment(
                        ManageBlockedUsersFragment()
                    )
                }
                LicensesFragment.ACTION -> {
                    setFragment(
                        LicensesFragment()
                    )
                }
                SupportUsFragment.ACTION -> {
                    setFragment(
                        SupportUsFragment()
                    )
                }
                ExperimentSettingsFragment.ACTION -> {
                    setFragment(
                        ExperimentSettingsFragment()
                    )
                }
                else -> {
                    Log.e(TAG, "Unsupported action $action. PreferenceActivity finished.")
                    finish()
                }
            }
        }
    }

    private fun setFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.preferenceContent, fragment)
        }
    }

}