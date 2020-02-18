package moe.feng.danmaqua.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.preference.SeekBarPreference
import androidx.preference.SwitchPreference
import kotlinx.coroutines.launch
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.R
import moe.feng.danmaqua.util.ext.onValueChanged

class FloatingSettingsFragment : BasePreferenceFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.FLOATING"

    }

    private val twoLinePref by preference<SwitchPreference>("floating_two_line")
    private val touchToMovePref by preference<SwitchPreference>("floating_touch_to_move")
    private val backgroundAlphaPref by preference<SeekBarPreference>("floating_background_alpha")
    private val textSizePref by preference<SeekBarPreference>("floating_text_size")

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preference_floating)

        twoLinePref.onValueChanged { value ->
            Settings.commit {
                floatingTwoLine = value
            }
            true
        }

        touchToMovePref.onValueChanged { value ->
            Settings.commit {
                floatingTouchToMove = value
            }
            true
        }

        backgroundAlphaPref.onValueChanged { value ->
            Settings.commit {
                floatingBackgroundAlpha = value.coerceIn(100..255)
            }
            true
        }

        textSizePref.onValueChanged { value ->
            Settings.commit {
                floatingTextSize = value.coerceIn(10..30)
            }
            true
        }

        updatePrefsValue()
    }

    private fun updatePrefsValue() = lifecycleScope.launch {
        twoLinePref.isChecked = Settings.floatingTwoLine
        touchToMovePref.isChecked = Settings.floatingTouchToMove
        backgroundAlphaPref.value = Settings.floatingBackgroundAlpha
        textSizePref.value = Settings.floatingTextSize
    }

    override fun onSettingsChanged() {
        updatePrefsValue()
    }

    override fun getActivityTitle(context: Context): CharSequence? {
        return context.getString(R.string.floating_settings_title)
    }

}