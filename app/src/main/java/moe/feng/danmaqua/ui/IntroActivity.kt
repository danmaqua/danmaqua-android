package moe.feng.danmaqua.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.intro_activity.*
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.R

class IntroActivity : BaseActivity() {

    private var allowInternetValue: Boolean = false
    private var allowBgServiceValue: Boolean = false
    private var allowCollectDataValue: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.intro_activity)

        allowInternet.text = HtmlCompat.fromHtml(
            getString(R.string.intro_allow_internet_access), 0
        )
        allowBgService.text = HtmlCompat.fromHtml(
            getString(R.string.intro_allow_background_service), 0
        )
        allowCollectData.text = HtmlCompat.fromHtml(
            getString(R.string.intro_allow_collect_data), 0
        )
        allowInternet.setOnCheckedChangeListener { _, isChecked ->
            allowInternetValue = isChecked
            updateStartButtonState()
        }
        allowBgService.setOnCheckedChangeListener { _, isChecked ->
            allowBgServiceValue = isChecked
            updateStartButtonState()
        }
        allowCollectData.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked && allowCollectDataValue) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.intro_collect_data_disabled_title)
                    .setMessage(R.string.intro_collect_data_disabled_message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
            allowCollectDataValue = isChecked
            updateStartButtonState()
        }

        updateStartButtonState()

        startButton.setOnClickListener {
            Danmaqua.Settings.introduced = true
            Danmaqua.Settings.enabledAnalytics = allowCollectData.isChecked
            Danmaqua.Settings.notifyChanged(this)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateStartButtonState() {
        startButton.isEnabled = allowInternetValue && allowBgServiceValue
    }

}