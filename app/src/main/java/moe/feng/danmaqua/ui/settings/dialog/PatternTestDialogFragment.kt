package moe.feng.danmaqua.ui.settings.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.*
import androidx.content.launchViewUrl
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.Danmaqua.EXTRA_PREFIX
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.BiliChatMessage
import moe.feng.danmaqua.ui.common.dialog.BaseDialogFragment
import moe.feng.danmaqua.util.DanmakuFilter
import moe.feng.danmaqua.model.flattenToString

class PatternTestDialogFragment : BaseDialogFragment() {

    companion object {

        const val ARGS_PATTERN = "args:PATTERN"
        const val ARGS_SAMPLE_TEXT = "args:SAMPLE_TEXT"

        private const val EXTRA_PATTERN = "$EXTRA_PREFIX.PATTERN"
        private const val EXTRA_SAMPLE_TEXT = "$EXTRA_PREFIX.SAMPLE_TEXT"

        fun newInstance(pattern: String = "", sampleText: String = ""): PatternTestDialogFragment {
            return PatternTestDialogFragment().apply {
                arguments = bundleOf(
                    ARGS_PATTERN to pattern,
                    ARGS_SAMPLE_TEXT to sampleText
                )
            }
        }

    }

    private var pattern: String = ""
    private var sampleText: String = ""

    private lateinit var regexpEdit: TextInputEditText
    private lateinit var sampleTextEdit: TextInputEditText
    private lateinit var statusView: TextView

    private var updateStatusJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            pattern = savedInstanceState.getString(
                EXTRA_PATTERN
            ) ?: pattern
            sampleText = savedInstanceState.getString(
                EXTRA_SAMPLE_TEXT
            ) ?: sampleText
        } else {
            arguments?.let {
                pattern = it.getString(
                    ARGS_PATTERN
                ) ?: pattern
                sampleText = it.getString(
                    ARGS_SAMPLE_TEXT
                ) ?: sampleText
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return buildAlertDialog {
            titleRes = R.string.filter_settings_test_pattern_title
            inflateView(R.layout.pattern_test_dialog_content) {
                onDialogViewCreated(it, savedInstanceState)
            }
            okButton()
            neutralButton(R.string.action_go_to_regexr_com) {
                context.launchViewUrl("https://regexr.com")
            }
        }
    }

    private fun onDialogViewCreated(view: View, savedInstanceState: Bundle?) {
        regexpEdit = view.findViewById(R.id.regexpEdit)
        sampleTextEdit = view.findViewById(R.id.sampleTextEdit)
        statusView = view.findViewById(R.id.statusView)

        regexpEdit.setText(pattern)
        sampleTextEdit.setText(sampleText)

        regexpEdit.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            pattern = text?.toString() ?: ""
            updateStatus()
        })
        sampleTextEdit.addTextChangedListener(onTextChanged = { text, _, _, _ ->
            sampleText = text?.toString() ?: ""
            updateStatus()
        })

        updateStatus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(
            EXTRA_PATTERN, pattern)
        outState.putString(
            EXTRA_SAMPLE_TEXT, sampleText)
    }

    private fun updateStatus() {
        updateStatusJob?.cancel()
        updateStatusJob = lifecycleScope.launch {
            val textResult = withContext(Dispatchers.IO) {
                try {
                    val danmakuFilter = DanmakuFilter.forTest(pattern)
                    val sampleDanmaku = BiliChatDanmaku(
                        cmd = BiliChatMessage.CMD_DANMAKU,
                        text = sampleText,
                        senderUid = 1L,
                        senderName = "",
                        timestamp = System.currentTimeMillis() / 2
                    )
                    if (danmakuFilter?.invoke(sampleDanmaku) == true) {
                        getString(R.string.test_pattern_dialog_status_matched,
                            sampleText,
                            danmakuFilter.unescapeSubtitle(sampleDanmaku)?.flattenToString())
                    } else {
                        getString(R.string.test_pattern_dialog_status_no_matches)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    getString(R.string.test_pattern_dialog_status_invalid_pattern)
                }
            }
            statusView.text = textResult
        }
    }

}