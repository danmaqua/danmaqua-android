package moe.feng.danmaqua.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.view_vtubers_catalog_activity.*
import kotlinx.coroutines.launch
import moe.feng.danmaqua.Danmaqua.EXTRA_DATA
import moe.feng.danmaqua.R
import moe.feng.danmaqua.api.DanmaquaApi
import moe.feng.danmaqua.event.OnCatalogSingleItemClickListener
import moe.feng.danmaqua.model.*
import moe.feng.danmaqua.ui.dialog.ConfirmSubscribeStreamerDialogFragment
import moe.feng.danmaqua.ui.list.CannotFindVTuberCatalogItemViewDelegate
import moe.feng.danmaqua.ui.list.VTuberSingleItemViewDelegate
import moe.feng.danmaqua.ui.subscription.showStreamerExistsDialog
import moe.feng.danmaqua.util.ext.*

class VTubersGroupActivity : BaseActivity(), OnCatalogSingleItemClickListener {

    companion object {

        const val STATE_GROUP = "state:GROUP"

    }

    private var vtuberGroup: VTuberGroup? = null

    private lateinit var groupArg: VTuberCatalog.Group

    private val adapter: MultiTypeAdapter = MultiTypeAdapter().also {
        it.register(VTuberSingleItemViewDelegate())
        it.register(CannotFindVTuberCatalogItemViewDelegate())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.view_vtubers_catalog_activity)

        intent?.getParcelableExtra<VTuberCatalog.Group>(EXTRA_DATA)?.let {
            groupArg = it
        } ?: run {
            Log.e(TAG, "VTubersGroupActivity requires data extra.")
            finish()
            return
        }

        setSupportActionBar(toolbar)
        title = groupArg.title
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.adapter = adapter

        reloadButton.onClick {
            vtuberGroup = null
            loadGroup()
        }

        if (savedInstanceState == null) {
            lifecycleScope.launch { loadGroup() }
        } else {
            vtuberGroup = savedInstanceState.getParcelable(STATE_GROUP)
            setViewStates(false)
        }

        eventsHelper.registerListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        vtuberGroup?.let { outState.putParcelable(STATE_GROUP, it) }
    }

    override fun onCatalogSingleItem(item: VTuberSingleItem) {
        launchWhenResumed {
            val subscription = database.subscriptions().findByUid(item.uid)
            if (subscription != null) {
                showStreamerExistsDialog(item.name)
            } else {
                ConfirmDialog.show(supportFragmentManager, item.toSubscription())
            }
        }
    }

    private suspend fun loadGroup() {
        setViewStates(loading = true)
        try {
            vtuberGroup = DanmaquaApi.getVTuberGroup(groupArg.name)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setViewStates(loading = false)
    }

    private fun setViewStates(loading: Boolean) {
        if (vtuberGroup != null) {
            progress.isGone = true
            recyclerView.isVisible = true
            failedView.isGone = true

            adapter.items = vtuberGroup!!.data.toMutableList() +
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

    class ConfirmDialog : ConfirmSubscribeStreamerDialogFragment() {

        companion object {

            const val ARGS_DATA = "args:DATA"

            fun show(fragmentManager: FragmentManager, subscription: Subscription) {
                val fragment = ConfirmDialog()
                fragment.arguments = bundleOf(ARGS_DATA to subscription)
                fragment.show(fragmentManager, "confirm_subscribe_streamer")
            }

        }

        override fun onPositiveButtonClick() {
            activity?.run {
                setResult(RESULT_OK, Intent().apply { putExtra(EXTRA_DATA, subscription) })
                finish()
            }
        }

    }

}