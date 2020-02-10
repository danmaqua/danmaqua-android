package moe.feng.danmaqua.ui.settings

import android.app.Activity
import android.content.*
import androidx.preference.PreferenceFragmentCompat
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import moe.feng.danmaqua.Danmaqua.ACTION_SETTINGS_UPDATED

abstract class BasePreferenceFragment : PreferenceFragmentCompat(), CoroutineScope by MainScope() {

    val defaultPreferences: SharedPreferences get() = MMKV.defaultMMKV()

    private val settingsUpdatedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!isDetached) {
                onSettingsUpdated()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(settingsUpdatedReceiver)
        this.cancel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.registerReceiver(settingsUpdatedReceiver, IntentFilter(ACTION_SETTINGS_UPDATED))
        val title = getActivityTitle(context)
        if (title != null) {
            val activity = if (context is Activity) context else this.activity
            activity?.title = title
        }
    }

    open fun getActivityTitle(context: Context): CharSequence? {
        return null
    }

    open fun onSettingsUpdated() {

    }

}