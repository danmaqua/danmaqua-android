package moe.feng.danmaqua.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.androidbrowserhelper.trusted.TwaLauncher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import moe.feng.danmaqua.Danmaqua.EXTRA_PREFIX
import moe.feng.danmaqua.R
import java.lang.Exception
import java.util.regex.Pattern

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
            pattern = savedInstanceState.getString(EXTRA_PATTERN) ?: pattern
            sampleText = savedInstanceState.getString(EXTRA_SAMPLE_TEXT) ?: sampleText
        } else {
            arguments?.let {
                pattern = it.getString(ARGS_PATTERN) ?: pattern
                sampleText = it.getString(ARGS_SAMPLE_TEXT) ?: sampleText
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = ContextThemeWrapper(activity!!,
            R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
        val view = LayoutInflater.from(context).inflate(R.layout.pattern_test_dialog_content, null)
        onDialogViewCreated(view, savedInstanceState)
        return AlertDialog.Builder(activity!!)
            .setTitle(R.string.filter_settings_test_pattern_title)
            .setView(view)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(R.string.action_go_to_regexr_com) { _, _ ->
                activity?.let {
                    TwaLauncher(it).launch("https://regexr.com".toUri())
                }
            }
            .create()
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
        outState.putString(EXTRA_PATTERN, pattern)
        outState.putString(EXTRA_SAMPLE_TEXT, sampleText)
    }

    private fun updateStatus() {
        updateStatusJob?.cancel()
        updateStatusJob = launch {
            try {
                val matcher = Pattern.compile(pattern).matcher(sampleText)
                if (matcher.matches() && matcher.groupCount() >= 1) {
                    statusView.text = getString(R.string.test_pattern_dialog_status_matched,
                        sampleText,
                        matcher.group(1))
                } else {
                    statusView.setText(R.string.test_pattern_dialog_status_no_matches)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                statusView.setText(R.string.test_pattern_dialog_status_invalid_pattern)
            }
        }
    }

}