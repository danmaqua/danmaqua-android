package moe.feng.danmaqua.ui.proxy

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.google.code.regexp.Pattern
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.api.bili.RoomApi
import moe.feng.danmaqua.api.bili.UserApi
import moe.feng.danmaqua.event.MainDrawerCallback
import moe.feng.danmaqua.event.OnConfirmSubscribeStreamerListener
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.service.DanmakuListenerService
import moe.feng.danmaqua.ui.common.BaseActivity
import moe.feng.danmaqua.ui.subscription.dialog.ConfirmSubscribeStreamerDialogFragment
import moe.feng.danmaqua.util.ext.eventsHelper

class LiveShareProxyActivity : BaseActivity(), OnConfirmSubscribeStreamerListener {

    companion object {

        const val REGEX = "(https?)://live\\.bilibili\\.com/(\\d+)"

        private const val CONFIRM_DIALOG_TAG = "LiveShareProxyActivity"

    }

    private var shouldFinishAfterOnCreate: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        eventsHelper.registerListener(this, CONFIRM_DIALOG_TAG)

        lifecycleScope.launch {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (text != null) {
                try {
                    val matcher = Pattern.compile(REGEX).matcher(text)
                    if (matcher.find()) {
                        val roomId = matcher.group(2).toLong()
                        val sub = database.subscriptions().findByRoomId(roomId)
                        if (sub != null) {
                            DanmakuListenerService.startServiceAndConnect(
                                this@LiveShareProxyActivity, roomId)
                        } else {
                            val toSub = getSubscription(roomId)
                            if (toSub != null) {
                                shouldFinishAfterOnCreate = false
                                launchWhenResumed {
                                    ConfirmSubscribeStreamerDialogFragment.show(
                                        supportFragmentManager,
                                        toSub,
                                        CONFIRM_DIALOG_TAG
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (shouldFinishAfterOnCreate) {
                finish()
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

    override fun onConfirmSubscribeStreamer(subscription: Subscription) {
        lifecycleScope.launch {
            val dao = database.subscriptions()
            if (dao.findByUid(subscription.uid) == null) {
                for (item in dao.getAll()) {
                    item.selected = false
                    dao.update(item)
                }
                subscription.selected = true
                dao.add(subscription)

                eventsHelper.of<MainDrawerCallback>().onSubscriptionChange(subscription)
            }
            DanmakuListenerService.startServiceAndConnect(
                this@LiveShareProxyActivity, subscription.roomId)
            finish()
        }
    }

    override fun onCancelConfirmSubscribe() {
        finish()
    }

}