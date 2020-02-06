package moe.feng.danmaqua.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import moe.feng.danmaqua.util.ext.TAG

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

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            when (val action = intent?.action) {
                FilterSettingsFragment.ACTION -> {
                    setFragment(FilterSettingsFragment())
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
            replace(android.R.id.content, fragment)
        }
    }

}