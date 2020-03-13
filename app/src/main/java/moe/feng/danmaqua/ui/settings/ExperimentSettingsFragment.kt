package moe.feng.danmaqua.ui.settings

import android.content.*
import android.content.pm.PackageManager
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_DISABLED
import android.content.pm.PackageManager.COMPONENT_ENABLED_STATE_ENABLED
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import com.google.firebase.iid.FirebaseInstanceId
import moe.feng.danmaqua.BuildConfig
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.proxy.B23ProxyActivity
import moe.feng.danmaqua.ui.proxy.LiveShareProxyActivity
import moe.feng.danmaqua.util.IntentUtils
import androidx.preference.onClick
import androidx.preference.onValueChanged

class ExperimentSettingsFragment : BasePreferenceFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.EXPERIMENT"

    }

    private var token: String? = null

    private val fbIdPref by preference<Preference>("firebase_instance_id")
    private val enabledAnalyticsPref by preference<CheckBoxPreference>("enabled_analytics")
    private val proxyB23Url by preference<CheckBoxPreference>("proxy_b23_url")
    private val proxyLiveShareUrl by preference<CheckBoxPreference>("proxy_live_share_url")

    override fun getActivityTitle(context: Context): CharSequence? {
        return context.getString(R.string.experiment_settings_title)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_dev)

        proxyB23Url.isChecked = isComponentEnabled(B23ProxyActivity::class.java)
        proxyB23Url.onValueChanged { value ->
            try {
                setComponentEnabled(B23ProxyActivity::class.java, value)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        proxyLiveShareUrl.isChecked = isComponentEnabled(LiveShareProxyActivity::class.java)
        proxyLiveShareUrl.onValueChanged { value ->
            try {
                setComponentEnabled(LiveShareProxyActivity::class.java, value)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        fbIdPref.isVisible = BuildConfig.DEBUG
        fbIdPref.setSummary(R.string.firebase_instance_id_not_registered_yet)
        fbIdPref.onClick {
            val context = context ?: return@onClick
            if (token == null) return@onClick
            val cm = context.getSystemService<ClipboardManager>()
            cm?.setPrimaryClip(ClipData.newPlainText("token", token))
            Toast.makeText(context,
                R.string.toast_copied_to_clipboard,
                Toast.LENGTH_LONG).show()
        }

        enabledAnalyticsPref.isChecked = Settings.enabledAnalytics
        enabledAnalyticsPref.onValueChanged { value ->
            Settings.commit {
                enabledAnalytics = value
            }
            true
        }

        setPreferenceClickListener("restart_to_intro") {
            Settings.introduced = false
            activity?.let {
                IntentUtils.restartApp(it)
            }
        }

        val instanceId = FirebaseInstanceId.getInstance().instanceId
        instanceId.addOnSuccessListener {
            fbIdPref.summary = it.token
            token = it.token
        }
    }

    private fun <T> isComponentEnabled(clazz: Class<T>): Boolean {
        val pm = context?.packageManager
        val component = ComponentName(requireContext(), clazz)
        return pm?.getComponentEnabledSetting(component) == COMPONENT_ENABLED_STATE_ENABLED
    }

    private fun <T> setComponentEnabled(clazz: Class<T>, enabled: Boolean) {
        requireContext().packageManager?.setComponentEnabledSetting(
            ComponentName(requireContext(), clazz),
            if (enabled) COMPONENT_ENABLED_STATE_ENABLED else COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }

}