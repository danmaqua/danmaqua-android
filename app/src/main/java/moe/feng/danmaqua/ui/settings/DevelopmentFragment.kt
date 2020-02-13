package moe.feng.danmaqua.ui.settings

import android.content.*
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import com.google.firebase.iid.FirebaseInstanceId
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.MainActivity

class DevelopmentFragment : BasePreferenceFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.DEVELOPMENT"

    }

    private var token: String? = null

    override fun getActivityTitle(context: Context): CharSequence? {
        return context.getString(R.string.development_settings_title)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_dev)

        val fbIdPref = findPreference<Preference>("firebase_instance_id")!!
        fbIdPref.setSummary(R.string.firebase_instance_id_not_registered_yet)
        fbIdPref.setOnPreferenceClickListener {
            val context = context ?: return@setOnPreferenceClickListener true
            if (token == null) return@setOnPreferenceClickListener true
            val cm = context.getSystemService<ClipboardManager>()
            cm?.setPrimaryClip(ClipData.newPlainText("token", token))
            Toast.makeText(context,
                R.string.toast_copied_to_clipboard,
                Toast.LENGTH_LONG).show()
            true
        }

        val enabledAnalyticsPref = findPreference<CheckBoxPreference>("enabled_analytics")!!
        enabledAnalyticsPref.isChecked = Danmaqua.Settings.enabledAnalytics
        enabledAnalyticsPref.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as Boolean
            Danmaqua.Settings.enabledAnalytics = value
            Danmaqua.Settings.notifyChanged(context!!)
            true
        }

        setPreferenceClickListener("restart_to_intro") {
            Danmaqua.Settings.introduced = false
            val intent = Intent.makeRestartActivityTask(ComponentName(context!!, MainActivity::class.java))
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            activity?.finish()
            Runtime.getRuntime().exit(0)
        }

        val instanceId = FirebaseInstanceId.getInstance().instanceId
        instanceId.addOnSuccessListener {
            fbIdPref.summary = it.token
            token = it.token
        }
    }

}