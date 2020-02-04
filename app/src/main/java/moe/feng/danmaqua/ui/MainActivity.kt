package moe.feng.danmaqua.ui

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.Danmaqua.EXTRA_ACTION
import moe.feng.danmaqua.IDanmakuListenerCallback
import moe.feng.danmaqua.IDanmakuListenerService
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.service.DanmakuListenerService
import moe.feng.danmaqua.ui.list.SimpleDanmakuItemViewDelegate
import moe.feng.danmaqua.util.ext.TAG

class MainActivity : BaseActivity(), DrawerViewFragment.Callback {

    private val danmakuListenerCallback: IDanmakuListenerCallback = DanmakuListenerCallbackImpl()

    private var service: IDanmakuListenerService? = null
    private var serviceConnection: ServiceConnection? = null

    private val danmakuList: MutableList<BiliChatDanmaku> = mutableListOf()
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

        startBindService()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceConnection?.let {
            try {
                unbindService(it)
            } catch (e: Exception) {
                e.printStackTrace()
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
        if (service?.isConnected == true) {
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
                        try {
                            service?.connect(current.roomId)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.e(TAG, "No subscriptions selected.")
                    }
                }
                true
            }
            R.id.action_disconnect -> {
                launch {
                    if (withContext(IO) { service?.isConnected } == true) {
                        try {
                            service?.disconnect()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSubscriptionChange(current: Subscription) {
        drawerLayout.closeDrawer(GravityCompat.START)
        launch {
            val needReconnect = withContext(IO) {
                service?.isConnected == true && service?.roomId != current.roomId
            }
            if (needReconnect) {
                service?.connect(current.roomId)
            }
        }
    }

    private fun startBindService() {
        val serviceIntent = Intent(this, DanmakuListenerService::class.java)
        serviceIntent.putExtra(EXTRA_ACTION, DanmakuListenerService.ACTION_START)
        ContextCompat.startForegroundService(this, serviceIntent)

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                if (binder == null) {
                    return
                }
                service = IDanmakuListenerService.Stub.asInterface(binder)
                service?.registerCallback(danmakuListenerCallback, false)
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                service = null
                invalidateOptionsMenu()
            }
        }
        this.serviceConnection = serviceConnection
        bindService(serviceIntent, serviceConnection, Service.BIND_AUTO_CREATE)
    }

    private inner class DanmakuListenerCallbackImpl : IDanmakuListenerCallback.Stub() {
        override fun onConnect(roomId: Long) {
            Log.i(TAG, "DanmakuListener: onConnect")
            invalidateOptionsMenu()
        }

        override fun onDisconnect() {
            Log.i(TAG, "DanmakuListener: onDisconnect")
            invalidateOptionsMenu()
        }

        override fun onReceiveDanmaku(msg: BiliChatDanmaku?) {
            Log.d(TAG, "DanmakuListener: onReceiveDanmaku: $msg")
            if (msg is BiliChatDanmaku) {
                danmakuList += msg
                danmakuAdapter.notifyDataSetChanged()
            }
        }

        override fun onHeartbeat(online: Int) {
            Log.d(TAG, "DanmakuListener: onHeartbeat: online=$online")
        }
    }

    private class SimpleDanmakuAdapter : MultiTypeAdapter() {

        init {
            register(SimpleDanmakuItemViewDelegate())
        }

    }

}