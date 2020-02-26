package moe.feng.danmaqua.ui.history

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DiffUtil
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.danmaku_history_activity.*
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.R
import moe.feng.danmaqua.data.HistoryManager
import moe.feng.danmaqua.model.HistoryFile
import moe.feng.danmaqua.ui.BaseActivity
import moe.feng.danmaqua.ui.list.HeaderItemViewDelegate
import moe.feng.danmaqua.ui.list.SimpleDiffItemCallback
import moe.feng.danmaqua.util.FileUtils
import moe.feng.danmaqua.util.ext.*
import java.io.File

class ManageHistoryActivity : BaseActivity() {

    companion object {

        private const val ACTION_SEND_TO = 0
        private const val ACTION_SAVE = 1

        private const val REQUEST_CODE_CREATE = 10

    }

    private val adapter: HistoryAdapter = HistoryAdapter()

    private var outputFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.danmaku_history_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.adapter = adapter

        setAdapterItems(emptyList())
        loadAdapterItems()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_CREATE && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                outputFile?.let {
                    launchWhenResumed {
                        try {
                            FileUtils.copyFileToUri(this@ManageHistoryActivity, it, uri)
                            showAlertDialog {
                                titleRes = R.string.export_success_dialog_title
                                message = getString(R.string.export_success_dialog_message,
                                    it.name, uri.toString())
                                okButton()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            showAlertDialog {
                                titleRes = R.string.export_failed_dialog_title
                                message = e.toString()
                                okButton()
                            }
                        }
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun loadAdapterItems() {
        launchWhenCreated {
            val historyFiles = HistoryManager.listHistoryFiles(this@ManageHistoryActivity)
            val subscriptions = database.subscriptions().getAll()
            setAdapterItems(historyFiles.map {
                HistoryItemViewDelegate.Item(
                    it,
                    subscriptions.find { s -> s.roomId == it.roomId }?.username ?:
                    getString(R.string.danmaku_history_unsubscribed_title)
                )
            })
        }
    }

    private fun setAdapterItems(items: List<HistoryItemViewDelegate.Item>) {
        val oldItems = adapter.items.toList()
        adapter.items = mutableListOf(ToggleViewDelegate.Item(Danmaqua.Settings.saveHistory)) +
                getString(R.string.danmaku_history_saved_files_title) +
                items
        DiffUtil.calculateDiff(SimpleDiffItemCallback(oldItems, adapter.items))
            .dispatchUpdatesTo(adapter)
    }

    fun onConfirmDelete(item: HistoryFile) {
        launchWhenResumed {
            if (HistoryManager.deleteRecord(item)) {
                loadAdapterItems()
            } else {
                Toast.makeText(
                    this@ManageHistoryActivity,
                    R.string.toast_danmaku_history_file_used,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun showExportDialog(item: HistoryFile) {
        showAlertDialog {
            var choice = 0
            titleRes = R.string.action_export
            setSingleChoiceItems(R.array.export_history_options, 0) { _, which ->
                choice = which
            }
            positiveButton(R.string.action_send_to) {
                onExportHistory(item, ACTION_SEND_TO, choice == 1)
            }
            negativeButton(R.string.action_save) {
                onExportHistory(item, ACTION_SAVE, choice == 1)
            }
            neutralButton(android.R.string.cancel)
        }
    }

    private fun onExportHistory(item: HistoryFile, option: Int, filtered: Boolean) {
        val activity = this@ManageHistoryActivity
        launchWhenCreated {
            val progress = ProgressDialog(activity).apply {
                setMessage(getString(R.string.export_progress_dialog_message))
                setCancelable(false)
            }
            launchWhenResumed { progress.show() }
            val file = if (filtered) {
                HistoryManager.getFilteredFile(activity, item)
            } else {
                item.file
            }
            launchWhenResumed { progress.cancel() }
            when (option) {
                ACTION_SEND_TO -> {
                    val contentUri = FileProvider.getUriForFile(
                        activity, Danmaqua.FILEPROVIDER_AUTHORITY, file)
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, contentUri)
                        type = "text/csv"
                    }
                    startActivity(Intent.createChooser(
                        sendIntent,
                        getString(R.string.action_send_to)
                    ))
                }
                ACTION_SAVE -> {
                    outputFile = file
                    val openIntent = Intent().apply {
                        action = Intent.ACTION_CREATE_DOCUMENT
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "text/csv"

                        putExtra(Intent.EXTRA_TITLE, file.name)
                    }
                    startActivityForResult(openIntent, REQUEST_CODE_CREATE)
                }
            }
        }
    }

    inner class HistoryAdapter :
        MultiTypeAdapter(), ToggleViewDelegate.Callback, HistoryItemViewDelegate.Callback {

        init {
            register(ToggleViewDelegate(this))
            register(HeaderItemViewDelegate())
            register(HistoryItemViewDelegate(this))
        }

        override fun onToggle() {
            Danmaqua.Settings.commit {
                saveHistory = !saveHistory
            }
            (adapter.items[0] as ToggleViewDelegate.Item).value = Danmaqua.Settings.saveHistory
            adapter.notifyItemChanged(0, Any())
        }

        override fun onHistoryItemClick(item: HistoryItemViewDelegate.Item) {
            ViewHistoryItemInfoDialogFragment.newInstance(item)
                .show(supportFragmentManager, "view_history_info")
        }

    }

}