package moe.feng.danmaqua.ui.proxy

import android.os.Bundle
import android.widget.Toast
import kotlinx.coroutines.runBlocking
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.Danmaqua.EXTRA_DATA
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.MainDrawerCallback
import moe.feng.danmaqua.service.DanmakuListenerService
import moe.feng.danmaqua.ui.common.BaseActivity
import androidx.content.eventsHelper

class QuickStartShortcutActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val roomId = intent?.getLongExtra(EXTRA_DATA, 0) ?: 0
        if (roomId > 0) {
            runBlocking {
                val dao = database.subscriptions()
                val subscription = dao.findByRoomId(roomId)
                if (subscription == null) {
                    Toast.makeText(this@QuickStartShortcutActivity,
                        R.string.toast_shortcut_subscription_removed, Toast.LENGTH_LONG).show()
                    return@runBlocking
                }
                dao.getAll().forEach {
                    it.selected = it.roomId == roomId
                    dao.update(it)
                }
                eventsHelper.of<MainDrawerCallback>().onSubscriptionChange(subscription)

                DanmakuListenerService.startServiceAndConnect(
                    this@QuickStartShortcutActivity, subscription.roomId)
            }
        }

        finish()
    }

}