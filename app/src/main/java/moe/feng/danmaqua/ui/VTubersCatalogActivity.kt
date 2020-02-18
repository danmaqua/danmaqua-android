package moe.feng.danmaqua.ui

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.view_vtubers_catalog_activity.*
import kotlinx.coroutines.launch
import moe.feng.danmaqua.Danmaqua.EXTRA_DATA
import moe.feng.danmaqua.R
import moe.feng.danmaqua.api.DanmaquaApi
import moe.feng.danmaqua.event.OnCatalogGroupItemClickListener
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.model.VTuberCatalog
import moe.feng.danmaqua.ui.NewSubscriptionActivity.Companion.REQUEST_CODE_CHOOSE_SUBSCRIPTION
import moe.feng.danmaqua.ui.list.CannotFindVTuberCatalogItemViewDelegate
import moe.feng.danmaqua.ui.list.VTuberCatalogItemViewDelegate
import moe.feng.danmaqua.util.ext.eventsHelper

class VTubersCatalogActivity : BaseActivity(), OnCatalogGroupItemClickListener {

    companion object {

        const val STATE_CATALOG = "state:CATALOG"

    }

    private var vtuberCatalog: VTuberCatalog? = null

    private val adapter: MultiTypeAdapter = MultiTypeAdapter().also {
        it.register(VTuberCatalogItemViewDelegate())
        it.register(CannotFindVTuberCatalogItemViewDelegate())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_vtubers_catalog_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.adapter = adapter

        reloadButton.onClick {
            vtuberCatalog = null
            loadCatalog()
        }

        if (savedInstanceState == null) {
            lifecycleScope.launch { loadCatalog() }
        } else {
            vtuberCatalog = savedInstanceState.getParcelable(STATE_CATALOG)
            setViewStates(false)
        }

        eventsHelper.registerListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        eventsHelper.unregisterListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        vtuberCatalog?.let { outState.putParcelable(STATE_CATALOG, it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE_SUBSCRIPTION && resultCode == RESULT_OK) {
            data?.getParcelableExtra<Subscription>(EXTRA_DATA)?.let {
                setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_DATA, it) })
                finish()
            }
        }
    }

    override fun onCatalogItemClick(item: VTuberCatalog.Group) {
        val intent = Intent(this, VTubersGroupActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        intent.putExtra(EXTRA_DATA, item)
        startActivityForResult(intent, REQUEST_CODE_CHOOSE_SUBSCRIPTION)
    }

    private suspend fun loadCatalog() {
        setViewStates(loading = true)
        try {
            vtuberCatalog = DanmaquaApi.getVTuberCatalog()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setViewStates(loading = false)
    }

    private fun setViewStates(loading: Boolean) {
        if (vtuberCatalog != null) {
            progress.isGone = true
            recyclerView.isVisible = true
            failedView.isGone = true

            adapter.items = vtuberCatalog!!.data.toMutableList() +
                    CannotFindVTuberCatalogItemViewDelegate.Item
            adapter.notifyDataSetChanged()
        } else {
            if (loading) {
                progress.isVisible = true
                recyclerView.isGone = true
                failedView.isGone = true
            } else {
                progress.isGone = true
                recyclerView.isGone = true
                failedView.isVisible = true
            }
        }
    }

}