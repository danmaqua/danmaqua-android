package moe.feng.danmaqua.ui

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceFragmentCompat
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BasePreferenceFragment : PreferenceFragmentCompat(), CoroutineScope by MainScope() {

    val defaultPreferences: SharedPreferences get() = MMKV.defaultMMKV()

    override fun onDestroy() {
        super.onDestroy()
        this.cancel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val title = getActivityTitle(context)
        if (title != null) {
            val activity = if (context is Activity) context else this.activity
            activity?.title = title
        }
    }

    open fun getActivityTitle(context: Context): CharSequence? {
        return null
    }

}