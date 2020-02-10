package moe.feng.danmaqua.ui

import android.app.Service
import android.content.*
import android.content.res.Configuration
import android.graphics.Rect
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.TooltipCompat
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.*
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bottom_toolbar_layout.*
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.Danmaqua.ACTION_SETTINGS_UPDATED
import moe.feng.danmaqua.Danmaqua.EXTRA_ACTION
import moe.feng.danmaqua.IDanmakuListenerCallback
import moe.feng.danmaqua.IDanmakuListenerService
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.service.DanmakuListenerService
import moe.feng.danmaqua.ui.dialog.NoConnectionsDialog
import moe.feng.danmaqua.ui.dialog.RoomInfoDialogFragment
import moe.feng.danmaqua.ui.list.AutoScrollHelper
import moe.feng.danmaqua.ui.list.MessageListAdapter
import moe.feng.danmaqua.ui.settings.FilterSettingsFragment
import moe.feng.danmaqua.util.*
import moe.feng.danmaqua.util.ext.TAG
import moe.feng.danmaqua.util.ext.compoundDrawableStartRes
import moe.feng.danmaqua.util.ext.screenHeight
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainActivity : BaseActivity(), DrawerViewFragment.Callback {

    companion object {

        const val REQUEST_CODE_OVERLAY_PERMISSION = 10

        const val STATE_ONLINE = "state:ONLINE"
        const val STATE_LIST_DATA = "state:LIST_DATA"

    }

    private val danmakuListenerCallback: IDanmakuListenerCallback = DanmakuListenerCallbackImpl()

    private val connectivityManager: ConnectivityManager? by lazy {
        getSystemService<ConnectivityManager>()
    }
    private val connectivityCallback: ConnectivityCallback = ConnectivityCallback()
    private var isConnectivityAvailable: Boolean = true

    private var service: IDanmakuListenerService? = null
    private var serviceConnection: ServiceConnection? = null

    private var online: Int = 0
    private var danmakuFilter: DanmakuFilter = DanmakuFilter.fromSettings()

    private lateinit var autoScrollHelper: AutoScrollHelper
    private val messageAdapter: MessageListAdapter = MessageListAdapter(onItemAdded = {
        if (!isFinishing && autoScrollHelper.autoScrollEnabled) {
            recyclerView.smoothScrollToPosition(it.itemCount)
        }
    })

    private lateinit var toolbarView: View
    private val avatarView by lazy { toolbarView.findViewById<ImageView>(R.id.avatarView) }
    private val usernameView by lazy { toolbarView.findViewById<TextView>(R.id.usernameView) }
    private val statusView by lazy { toolbarView.findViewById<TextView>(R.id.statusView) }

    private val onSettingsUpdated = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            danmakuFilter = DanmakuFilter.fromSettings()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        // Make status bar and nav bar transparent
        setWindowFlags()

        // Set up toolbar
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
        coordinator.setOnApplyWindowInsetsListener { _, insets ->
            appBarLayout.updatePadding(top = insets.systemWindowInsetTop)
            bottomAppBarBackground.updateLayoutParams { height = insets.systemWindowInsetBottom }
            if (insets.systemWindowInsetBottom > 0) {
                if (!hideNavigation) setWindowFlags(hideNavigation = true)
            } else {
                if (hideNavigation) setWindowFlags(hideNavigation = false)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (insets.systemGestureInsets.bottom != insets.tappableElementInsets.bottom) {
                    bottomAppBarBackground.alpha = 0F
                } else {
                    bottomAppBarBackground.alpha = 1F
                }
            } else {
                bottomAppBarBackground.alpha = 1F
            }
            insets
        }
        bottomAppBar.addOnLayoutChangeListener { _, _, top, _, bottom, _, _, _, _ ->
            recyclerView.updatePadding(bottom = bottom - top)
            backToLatestButtonContainer.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                bottomMargin = bottom - top
            }
        }

        // Set up views visibility
        backToLatestButton.isGone = true

        // Initialize message list views
        recyclerView.adapter = messageAdapter
        autoScrollHelper = AutoScrollHelper.create(recyclerView)
        recyclerView.addOnScrollListener(ScrollToLatestButtonScrollListener())

        // Set up event listener
        drawerLayout.addDrawerListener(MainDrawerListener())
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

        updateGestureExclusion()
        updateAvatarAndNameViews()
        updateStatusViews()
        checkServiceStatus()

        registerReceiver(onSettingsUpdated, IntentFilter(ACTION_SETTINGS_UPDATED))
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager?.registerDefaultNetworkCallback(connectivityCallback)
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager?.unregisterNetworkCallback(connectivityCallback)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateGestureExclusion()
        setWindowFlags()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_ONLINE, online)
        outState.putParcelableArrayList(STATE_LIST_DATA, ArrayList(messageAdapter.list))
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
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
        unregisterReceiver(onSettingsUpdated)
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
                    askShowFloatingWindow()
                } else {
                    // TODO Show fail
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
            R.id.action_room_info -> {
                launch {
                    val current = database.subscriptions().getAll().firstOrNull { it.selected }
                    if (current != null) {
                        RoomInfoDialogFragment.newInstance(current.roomId)
                            .show(supportFragmentManager, "room_info")
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

    override fun setWindowFlags(lightNavBar: Boolean?, hideNavigation: Boolean?) {
        super.setWindowFlags(
            lightNavBar ?: drawerLayout.isDrawerOpen(GravityCompat.START),
            hideNavigation)
    }

    private fun updateGestureExclusion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val screenHeight = windowManager.defaultDisplay.screenHeight
            val height = resources.getDimensionPixelSize(R.dimen.max_gesture_exclusion_height)
            val width = resources.getDimensionPixelSize(R.dimen.drawer_layout_start_area_width)
            drawerLayout.systemGestureExclusionRects = listOf(
                Rect(0, (screenHeight - height) / 2, width, (screenHeight + height) / 2)
            )
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
            askShowFloatingWindow()
        }
    }

    private fun askShowFloatingWindow() = launch {
        if (withContext(IO) { service?.isFloatingShowing } == true) {
            Toast.makeText(this@MainActivity,
                R.string.toast_floating_is_showing, Toast.LENGTH_SHORT).show()
            // TODO Highlight floating window animation
            return@launch
        }
        AlertDialog.Builder(this@MainActivity)
            .setTitle(R.string.ask_show_floating_title)
            .setMessage(R.string.ask_show_floating_message)
            .setPositiveButton(R.string.action_minimize) { _, _ ->
                this@MainActivity.launch { showFloatingWindow() }
                moveTaskToBack(true)
            }
            .setNegativeButton(R.string.action_stay_here) { _, _ ->
                this@MainActivity.launch { showFloatingWindow() }
            }
            .setNeutralButton(android.R.string.cancel, null)
            .show()
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
                val showConnectivityDialog: Boolean
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    showConnectivityDialog = !isConnectivityAvailable
                } else {
                    showConnectivityDialog = connectivityManager?.isDefaultNetworkActive != true
                }
                if (showConnectivityDialog) {
                    NoConnectionsDialog.show(this@MainActivity) {
                        launch {
                            connectToCurrentSubscription()
                        }
                    }
                    return@launch
                }
                connectToCurrentSubscription()
            }
        }
    }

    private suspend fun connectToCurrentSubscription() = withContext(IO) {
        val current = database.subscriptions().getAll().firstOrNull { it.selected }
        if (current != null) {
            connectRoom(current.roomId)
        } else {
            Log.e(TAG, "No subscriptions selected.")
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
            Picasso.get().load(cur.avatar)
                .placeholder(R.drawable.avatar_placeholder_empty)
                .into(avatarView)
        } else {
            usernameView.setText(R.string.no_streamer_selected_title)
            Picasso.get().cancelRequest(avatarView)
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
            messageAdapter.addSystemMessage(getString(R.string.sys_msg_connected_to_room, roomId))
            updateStatusViews()
        }

        override fun onDisconnect() {
            messageAdapter.addSystemMessage(getString(R.string.sys_msg_disconnected))
            updateStatusViews()
        }

        override fun onReceiveDanmaku(msg: BiliChatDanmaku) {
            launch {
                if (withContext(IO) { danmakuFilter(msg) }) {
                    messageAdapter.addDanmaku(msg)
                }
            }
        }

        override fun onHeartbeat(online: Int) {
            this@MainActivity.online = online
            updateStatusViews()
        }

    }

    private inner class ScrollToLatestButtonScrollListener : RecyclerView.OnScrollListener() {

        var state: Int = RecyclerView.SCROLL_STATE_IDLE

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            state = newState
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

    }

    private inner class MainDrawerListener : DrawerLayout.DrawerListener {

        override fun onDrawerStateChanged(newState: Int) {}

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            if (slideOffset > 0.5F) {
                if (!lightNavBar) {
                    setWindowFlags(lightNavBar = true)
                }
            } else {
                if (lightNavBar) {
                    setWindowFlags(lightNavBar = false)
                }
            }
        }

        override fun onDrawerClosed(drawerView: View) {}

        override fun onDrawerOpened(drawerView: View) {}

    }

    private inner class ConnectivityCallback : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            isConnectivityAvailable = true
        }

        override fun onUnavailable() {
            isConnectivityAvailable = false
        }

        override fun onLost(network: Network) {
            isConnectivityAvailable = false
        }

    }

}