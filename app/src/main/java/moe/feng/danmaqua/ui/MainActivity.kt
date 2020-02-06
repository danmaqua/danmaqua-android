package moe.feng.danmaqua.ui

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.bottom_toolbar_layout.*
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
import moe.feng.danmaqua.util.HttpUtils
import moe.feng.danmaqua.util.IntentUtils
import moe.feng.danmaqua.util.ext.TAG
import moe.feng.danmaqua.util.ext.compoundDrawableStartRes
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : BaseActivity(), DrawerViewFragment.Callback {

    private val danmakuListenerCallback: IDanmakuListenerCallback = DanmakuListenerCallbackImpl()

    private var service: IDanmakuListenerService? = null
    private var serviceConnection: ServiceConnection? = null

    private var online: Int = 0

    private val danmakuList: MutableList<BiliChatDanmaku> = mutableListOf()
    private val danmakuAdapter: SimpleDanmakuAdapter = SimpleDanmakuAdapter().also {
        it.items = danmakuList
    }

    private lateinit var toolbarView: View
    private val avatarView by lazy { toolbarView.findViewById<ImageView>(R.id.avatarView) }
    private val usernameView by lazy { toolbarView.findViewById<TextView>(R.id.usernameView) }
    private val statusView by lazy { toolbarView.findViewById<TextView>(R.id.statusView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.also {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayShowCustomEnabled(true)
        }
        toolbarView = LayoutInflater.from(this).inflate(R.layout.main_toolbar_layout, null)
        toolbarView.findViewById<View>(R.id.titleButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        supportActionBar?.customView = toolbarView

        recyclerView.adapter = danmakuAdapter

        TooltipCompat.setTooltipText(fab, getString(R.string.action_open_a_floating_window))

        connectButton.setOnClickListener(this::onConnectButtonClick)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.drawerView, DrawerViewFragment())
            }
        }

        updateAvatarAndNameViews()
        updateStatusViews()
        checkServiceStatus()
    }

    override fun onDestroy() {
        super.onDestroy()
        service?.unregisterCallback(danmakuListenerCallback)
        if (service?.isConnected != true) {
            stopListenerService()
        }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_stream -> {
                launch {
                    val current = database.subscriptions().getAll().firstOrNull { it.selected }
                    if (current != null) {
                        startActivity(IntentUtils.openBilibiliLive(
                            this@MainActivity, current.roomId))
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSubscriptionChange(current: Subscription) {
        drawerLayout.closeDrawer(GravityCompat.START)
        updateAvatarAndNameViews()
        launch {
            val needReconnect = withContext(IO) {
                service?.isConnected == true && service?.roomId != current.roomId
            }
            if (needReconnect) {
                connectRoom(current.roomId)
            }
        }
    }

    private fun onConnectButtonClick(view: View) {
        launch {
            if (withContext(IO) { service?.isConnected } == true) {
                try {
                    service?.disconnect()
                } catch (e: Exception) {
                    e.printStackTrace()
                    updateStatusViews()
                }
            } else {
                val current = database.subscriptions().getAll().firstOrNull { it.selected }
                if (current != null) {
                    connectRoom(current.roomId)
                } else {
                    Log.e(TAG, "No subscriptions selected.")
                }
            }
        }
    }

    private suspend fun connectRoom(roomId: Long) = withContext(IO) {
        if (service == null) {
            startForegroundListenerService()
            service = suspendCoroutine<IDanmakuListenerService> { c ->
                bindListenerService { c.resume(it) }
            }
        }
        try {
            service?.connect(roomId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkServiceStatus() {
        val serviceIntent = Intent(this, DanmakuListenerService::class.java)
        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                if (binder == null) {
                    return
                }
                val service = IDanmakuListenerService.Stub.asInterface(binder)
                if (service?.isConnected == true) {
                    bindListenerService()
                }
                unbindService(this)
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }
        if (!bindService(serviceIntent, serviceConnection, Service.BIND_AUTO_CREATE)) {
            unbindService(serviceConnection)
        }
    }

    private fun startForegroundListenerService() {
        val serviceIntent = Intent(this, DanmakuListenerService::class.java)
        serviceIntent.putExtra(EXTRA_ACTION, DanmakuListenerService.ACTION_START)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopListenerService() {
        val serviceIntent = Intent(this, DanmakuListenerService::class.java)
        serviceIntent.putExtra(EXTRA_ACTION, DanmakuListenerService.ACTION_STOP)
        startService(serviceIntent)

        serviceConnection?.let {
            try {
                unbindService(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        service = null
        serviceConnection = null
    }

    private fun bindListenerService(onConnected: (IDanmakuListenerService) -> Unit = {}) {
        val serviceIntent = Intent(this, DanmakuListenerService::class.java)

        val serviceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                if (binder == null) {
                    return
                }
                service = IDanmakuListenerService.Stub.asInterface(binder).also {
                    it.requestHeartbeat()
                    it.registerCallback(danmakuListenerCallback, false)
                    if (it.isConnected) {
                        updateStatusViews()
                    }
                    onConnected(it)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                service = null
                updateStatusViews()
            }
        }
        this.serviceConnection = serviceConnection
        if (!bindService(serviceIntent, serviceConnection, 0)) {
            startForegroundListenerService()
        }
    }

    private fun updateAvatarAndNameViews() = launch {
        val cur = database.subscriptions().findSelected()
        if (cur != null) {
            usernameView.text = cur.username
            avatarView.setImageBitmap(HttpUtils.loadBitmapWithCache(cur.avatar))
        } else {
            usernameView.setText(R.string.no_streamer_selected_title)
            avatarView.setImageResource(R.drawable.avatar_placeholder_empty)
        }
    }

    private fun updateStatusViews() = launch {
        if (withContext(IO) { service?.isConnected } == true) {
            connectButton.setText(R.string.action_disconnect)
            connectButton.compoundDrawableStartRes = R.drawable.ic_stop_24

            statusView.isVisible = true
            statusView.text = getString(R.string.status_connected_with_popularity, online)
        } else {
            connectButton.setText(R.string.action_connect)
            connectButton.compoundDrawableStartRes = R.drawable.ic_play_circle_filled_24

            if (database.subscriptions().findSelected() == null) {
                statusView.isGone = true
            } else {
                statusView.isVisible = true
                statusView.setText(R.string.status_disconnected)
            }
        }
    }

    private inner class DanmakuListenerCallbackImpl : IDanmakuListenerCallback.Stub() {
        override fun onConnect(roomId: Long) {
            Log.i(TAG, "DanmakuListener: onConnect")
            updateStatusViews()
        }

        override fun onDisconnect() {
            Log.i(TAG, "DanmakuListener: onDisconnect")
            updateStatusViews()
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
            this@MainActivity.online = online
            updateStatusViews()
        }
    }

    private class SimpleDanmakuAdapter : MultiTypeAdapter() {

        init {
            register(SimpleDanmakuItemViewDelegate())
        }

    }

}