package moe.feng.danmaqua.ui.subscription

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.new_subscription_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.Danmaqua.EXTRA_DATA
import moe.feng.danmaqua.R
import moe.feng.danmaqua.api.DanmaquaApi
import moe.feng.danmaqua.api.bili.RoomApi
import moe.feng.danmaqua.api.bili.UserApi
import moe.feng.danmaqua.event.OnConfirmSubscribeStreamerListener
import moe.feng.danmaqua.event.OnRecommendedStreamerItemClickListener
import moe.feng.danmaqua.model.Recommendation
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.model.toSubscription
import moe.feng.danmaqua.ui.common.BaseActivity
import moe.feng.danmaqua.ui.subscription.dialog.showConfirmSubscribeStreamerDialog
import moe.feng.danmaqua.ui.subscription.dialog.showSearchNoResultDialog
import moe.feng.danmaqua.ui.subscription.dialog.showStreamerExistsDialog
import moe.feng.danmaqua.ui.subscription.list.RecommendedStreamerItemViewDelegate
import moe.feng.danmaqua.util.ext.eventsHelper

class NewSubscriptionActivity : BaseActivity(),
    OnRecommendedStreamerItemClickListener, OnConfirmSubscribeStreamerListener {

    companion object {

        private const val STATE_RECOMMENDATION = "state:RECOMMENDATION"

        const val REQUEST_CODE_CHOOSE_SUBSCRIPTION = 10

    }

    private var recommendation: Recommendation? = null

    private val adapter: MultiTypeAdapter = MultiTypeAdapter().also {
        it.register(
            RecommendedStreamerItemViewDelegate()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_subscription_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        roomIdEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchButton.performClick()
                true
            } else {
                false
            }
        }

        TooltipCompat.setTooltipText(searchButton, getString(R.string.action_search_room_id))
        searchButton.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                val id = roomIdEdit.text.toString().trim().toLongOrNull() ?: 0L
                if (id <= 0) {
                    Toast.makeText(
                        this@NewSubscriptionActivity,
                        R.string.toast_invalid_room_id,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                val idResultInDB = database.subscriptions().findByUid(id)
                if (idResultInDB != null) {
                    showStreamerExistsDialog(idResultInDB.username)
                    return@launchWhenResumed
                }
                try {
                    val subscription = getSubscription(id)
                    if (subscription != null) {
                        showConfirmSubscribeStreamerDialog(subscription)
                        return@launchWhenResumed
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                showSearchNoResultDialog()
            }
        }

        recommendationList.adapter = adapter

        reloadButton.onClick {
            recommendation = null
            loadRecommendation()
        }

        viewCatalogButton.setOnClickListener {
            val intent = Intent(this, VTubersCatalogActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            startActivityForResult(intent,
                REQUEST_CODE_CHOOSE_SUBSCRIPTION
            )
        }

        if (savedInstanceState == null) {
            lifecycleScope.launch { loadRecommendation() }
        } else {
            recommendation = savedInstanceState.getParcelable(
                STATE_RECOMMENDATION
            )
            setRecommendationViews(false)
        }

        eventsHelper.registerListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        eventsHelper.unregisterListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        recommendation?.let { outState.putParcelable(
            STATE_RECOMMENDATION, it) }
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

    override fun onRecommendedStreamerItemClick(item: Recommendation.Item) {
        launchWhenResumed {
            val subscription = database.subscriptions().findByUid(item.uid)
            if (subscription != null) {
                showStreamerExistsDialog(item.name)
            } else {
                showConfirmSubscribeStreamerDialog(item.toSubscription())
            }
        }
    }

    override fun onConfirmSubscribeStreamer(subscription: Subscription) {
        val result = Intent()
        result.putExtra(EXTRA_DATA, subscription)
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    override fun onCancelConfirmSubscribe() {

    }

    private suspend fun loadRecommendation() {
        setRecommendationViews(true)
        try {
            recommendation = DanmaquaApi.getRecommendation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setRecommendationViews(false)
    }

    private fun setRecommendationViews(loading: Boolean = false) {
        if (recommendation != null) {
            recommendationProgress.isGone = true
            recommendationList.isVisible = true
            recommendationFailedView.isGone = true

            adapter.items = recommendation!!.data
            adapter.notifyDataSetChanged()
        } else {
            if (loading) {
                recommendationProgress.isVisible = true
                recommendationList.isGone = true
                recommendationFailedView.isGone = true
            } else {
                recommendationProgress.isGone = true
                recommendationList.isGone = true
                recommendationFailedView.isVisible = true
            }
        }
    }

    private suspend fun getSubscription(roomId: Long): Subscription? = withContext(Dispatchers.IO) {
        try {
            val roomInitInfo = RoomApi.getRoomInitInfo(roomId)
            val spaceInfo = UserApi.getSpaceInfo(roomInitInfo.data.uid)
            Subscription(spaceInfo.data.uid, roomId, spaceInfo.data.name, spaceInfo.data.face)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}