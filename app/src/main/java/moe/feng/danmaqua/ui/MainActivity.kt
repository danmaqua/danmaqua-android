package moe.feng.danmaqua.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.launch
import moe.feng.danmaqua.R
import moe.feng.danmaqua.api.DanmakuApi
import moe.feng.danmaqua.api.DanmakuListener
import moe.feng.danmaqua.api.RoomApi
import moe.feng.danmaqua.model.BiliChatMessage
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.ui.list.SimpleDanmakuItemViewDelegate
import moe.feng.danmaqua.util.ext.TAG

class MainActivity : BaseActivity(), DanmakuListener.Callback, DrawerViewFragment.Callback {

    private var danmakuListener: DanmakuListener? = null

    private val danmakuList: MutableList<BiliChatMessage.Danmaku> = mutableListOf()
    private val danmakuAdapter: SimpleDanmakuAdapter = SimpleDanmakuAdapter().also {
        it.items = danmakuList
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setSupportActionBar(toolbar)

        recyclerView.adapter = danmakuAdapter

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.drawerView, DrawerViewFragment())
            }
        }
    }

    override fun onAttachFragment(fragment: Fragment) {
        if (fragment is DrawerViewFragment) {
            fragment.callback = this
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_app_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val connectItem = menu.findItem(R.id.action_connect)
        val disconnectItem = menu.findItem(R.id.action_disconnect)
        if (danmakuListener?.isConnected == true) {
            connectItem.isVisible = false
            disconnectItem.isVisible = true
        } else {
            connectItem.isVisible = true
            disconnectItem.isVisible = false
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_connect -> {
                launch {
                    val current = database.subscriptions().getAll().firstOrNull { it.selected }
                    if (current != null) {
                        danmakuListener = DanmakuApi.listen(current.roomId, this@MainActivity)
                    } else {
                        Log.e(TAG, "No subscriptions selected.")
                    }
                }
                true
            }
            R.id.action_disconnect -> {
                if (danmakuListener?.isClosed == false) {
                    danmakuListener?.close()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onConnect() {
        Log.i(TAG, "DanmakuListener: onConnect")
        invalidateOptionsMenu()
    }

    override fun onDisconnect(userReason: Boolean) {
        Log.i(TAG, "DanmakuListener: onDisconnect: userReason=$userReason")
        invalidateOptionsMenu()
    }

    override fun onHeartbeat(online: Int) {
        Log.d(TAG, "DanmakuListener: onHeartbeat: online=$online")
    }

    override fun onMessage(msg: BiliChatMessage) {
        Log.d(TAG, "DanmakuListener: onMessage: $msg")
        if (msg is BiliChatMessage.Danmaku) {
            danmakuList += msg
            danmakuAdapter.notifyDataSetChanged()
        }
    }

    override fun onFailure(t: Throwable) {
        Log.e(TAG, "DanmakuListener: onFailure", t)
    }

    override fun onSubscriptionChange(current: Subscription) {
        Log.d(TAG, "onSubscriptionChange -> $current")
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private class SimpleDanmakuAdapter : MultiTypeAdapter() {

        init {
            register(SimpleDanmakuItemViewDelegate())
        }

    }

}