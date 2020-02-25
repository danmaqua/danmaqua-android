package moe.feng.danmaqua.ui.history

import android.os.Bundle
import android.widget.Toast
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

class ManageHistoryActivity : BaseActivity() {

    private val adapter: HistoryAdapter = HistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.danmaku_history_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.adapter = adapter

        setAdapterItems(emptyList())
        loadAdapterItems()
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