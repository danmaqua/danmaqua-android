package moe.feng.danmaqua.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.core.text.HtmlCompat
import kotlinx.android.synthetic.main.intro_activity.*
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.MainActivity
import moe.feng.danmaqua.ui.common.BaseActivity
import androidx.appcompat.app.messageRes
import androidx.appcompat.app.okButton
import androidx.appcompat.app.showAlertDialog
import androidx.appcompat.app.titleRes

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
                showAlertDialog {
                    titleRes = R.string.intro_collect_data_disabled_title
                    messageRes = R.string.intro_collect_data_disabled_message
                    okButton()
                }
            }
            allowCollectDataValue = isChecked
            updateStartButtonState()
        }

        updateStartButtonState()

        startButton.setOnClickListener {
            Settings.commit {
                introduced = true
                enabledAnalytics = allowCollectData.isChecked
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateStartButtonState() {
        startButton.isEnabled = allowInternetValue && allowBgServiceValue
    }

}