package moe.feng.danmaqua.ui

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.TooltipCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.bottom_toolbar_layout.*
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.Danmaqua.EXTRA_ACTION
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.IDanmakuListenerCallback
import moe.feng.danmaqua.IDanmakuListenerService
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.service.DanmakuListenerService
import moe.feng.danmaqua.ui.list.MessageListAdapter
import moe.feng.danmaqua.util.HttpUtils
import moe.feng.danmaqua.util.IntentUtils
import moe.feng.danmaqua.util.WindowUtils
import moe.feng.danmaqua.util.ext.TAG
import moe.feng.danmaqua.util.ext.compoundDrawableStartRes
import java.util.regex.Pattern
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : BaseActivity(), DrawerViewFragment.Callback {

    companion object {

        const val REQUEST_CODE_OVERLAY_PERMISSION = 10

        const val STATE_ONLINE = "state:ONLINE"
        const val STATE_LIST_DATA = "state:LIST_DATA"

    }

    private val danmakuListenerCallback: IDanmakuListenerCallback = DanmakuListenerCallbackImpl()

    private var service: IDanmakuListenerService? = null
    private var serviceConnection: ServiceConnection? = null

    private var online: Int = 0

    private val messageAdapter: MessageListAdapter = MessageListAdapter(onItemAdded = {
        if (!isFinishing && autoScrollToLatest) {
            recyclerView.smoothScrollToPosition(it.itemCount)
        }
    })
    private var autoScrollToLatest: Boolean = true

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
        toolbarView = LayoutInflater.from(this)
            .inflate(R.layout.main_toolbar_layout, toolbar, false)
        toolbarView.findViewById<View>(R.id.titleButton).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        supportActionBar?.customView = toolbarView
        backToLatestButton.isGone = true

        recyclerView.adapter = messageAdapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            var state: Int = RecyclerView.SCROLL_STATE_IDLE

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                state = newState
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val lastPos = layoutManager.findLastCompletelyVisibleItemPosition()
                        val itemCount = layoutManager.itemCount
                        if (lastPos == itemCount - 1) {
                            autoScrollToLatest = true
                        }
                    }
                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        autoScrollToLatest = false
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 5) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPos = layoutManager.findLastCompletelyVisibleItemPosition()
                    val itemCount = layoutManager.itemCount

                    if (itemCount < 3 || ((itemCount - 1) - lastPos < 3)) {
                        backToLatestButton.isGone = true
                    } else if (state == RecyclerView.SCROLL_STATE_DRAGGING) {
                        backToLatestButton.isVisible = true
                    }
                } else if (dy < 0) {
                    backToLatestButton.isGone = true
                }
            }
        })

        TooltipCompat.setTooltipText(fab, getString(R.string.action_open_a_floating_window))

        connectButton.setOnClickListener(this::onConnectButtonClick)
        setFilterButton.setOnClickListener {
            PreferenceActivity.launch(this, FilterSettingsFragment.ACTION)
        }
        backToLatestButton.setOnClickListener {
            backToLatestButton.isGone = true
            recyclerView.smoothScrollToPosition(messageAdapter.itemCount - 1)
        }
        fab.setOnClickListener(this::onFabClick)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.drawerView, DrawerViewFragment())
            }
        } else {
            online = savedInstanceState.getInt(STATE_ONLINE)
            savedInstanceState.getParcelableArrayList<Parcelable>(STATE_LIST_DATA)?.let {
                messageAdapter.list.clear()
                messageAdapter.list.addAll(it)
            }
        }

        updateAvatarAndNameViews()
        updateStatusViews()
        checkServiceStatus()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_ONLINE, online)
        outState.putParcelableArrayList(STATE_LIST_DATA, ArrayList(messageAdapter.list))
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_OVERLAY_PERMISSION -> {
                if (WindowUtils.canDrawOverlays(this)) {
                    launch { showFloatingWindow() }
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
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

    private fun onFabClick(view: View) {
        launch {
            val activity = this@MainActivity
            if (withContext(IO) { service?.isConnected } != true) {
                Toast.makeText(
                    activity,
                    R.string.toast_connect_room_before_opening_float,
                    Toast.LENGTH_SHORT
                ).show()
                return@launch
            }
            if (!WindowUtils.canDrawOverlays(activity)) {
                AlertDialog.Builder(activity)
                    .setTitle(R.string.overlay_permission_request_title)
                    .setMessage(R.string.overlay_permission_request_message)
                    .setPositiveButton(R.string.action_allow) { _, _ ->
                        WindowUtils.requestOverlayPermission(
                            activity, REQUEST_CODE_OVERLAY_PERMISSION)
                    }
                    .setNegativeButton(R.string.action_deny, null)
                    .show()
                return@launch
            }
            showFloatingWindow()
        }
    }

    private suspend fun showFloatingWindow() = withContext(IO) {
        if (service == null) {
            startForegroundListenerService()
            service = suspendCoroutine<IDanmakuListenerService> { c ->
                bindListenerService { c.resume(it) }
            }
        }
        try {
            service?.showFloating()
        } catch (e: Exception) {
            e.printStackTrace()
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
            messageAdapter.addSystemMessage(getString(R.string.sys_msg_connected_to_room, roomId))
            updateStatusViews()
        }

        override fun onDisconnect() {
            Log.i(TAG, "DanmakuListener: onDisconnect")
            messageAdapter.addSystemMessage(getString(R.string.sys_msg_disconnected))
            updateStatusViews()
        }

        override fun onReceiveDanmaku(msg: BiliChatDanmaku) {
            Log.d(TAG, "DanmakuListener: onReceiveDanmaku: $msg")
            launch {
                if (Settings.Filter.enabled) {
                    val ignored = withContext(IO) {
                        try {
                            val pattern = Pattern.compile(Settings.Filter.pattern)
                            val matcher = pattern.matcher(msg.text)
                            !matcher.matches()
                        } catch (e: Exception) {
                            Log.e(TAG, "Filter pattern might be invalid.")
                            true
                        }
                    }
                    if (ignored) {
                        return@launch
                    }
                }
                messageAdapter.addDanmaku(msg)
            }
        }

        override fun onHeartbeat(online: Int) {
            Log.d(TAG, "DanmakuListener: onHeartbeat: online=$online")
            this@MainActivity.online = online
            updateStatusViews()
        }
    }

}