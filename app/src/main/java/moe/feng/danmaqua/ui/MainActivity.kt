package moe.feng.danmaqua.ui

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.widget.compoundDrawableStartRes
import androidx.appcompat.widget.TooltipCompat
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.*
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.bottom_toolbar_layout.*
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.IDanmakuListenerCallback
import moe.feng.danmaqua.R
import moe.feng.danmaqua.api.bili.UserApi
import moe.feng.danmaqua.event.MainDanmakuContextMenuListener
import moe.feng.danmaqua.event.MainDrawerCallback
import moe.feng.danmaqua.event.NoConnectionsDialogListener
import moe.feng.danmaqua.event.SettingsChangedListener
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.BlockedTextRule
import moe.feng.danmaqua.model.BlockedUserRule
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.ui.common.BaseActivity
import moe.feng.danmaqua.ui.common.list.AutoScrollHelper
import moe.feng.danmaqua.ui.intro.IntroActivity
import moe.feng.danmaqua.ui.main.*
import moe.feng.danmaqua.ui.main.dialog.*
import moe.feng.danmaqua.ui.main.list.MessageListAdapter
import moe.feng.danmaqua.util.*
import androidx.view.avatarUrl
import androidx.content.eventsHelper
import androidx.content.packageVersionCode
import androidx.view.screenHeight

class MainActivity : BaseActivity(),
    SettingsChangedListener, MainDanmakuContextMenuListener, MainDrawerCallback {

    companion object {

        const val REQUEST_CODE_OVERLAY_PERMISSION = 10

        const val STATE_ONLINE = "state:ONLINE"
        const val STATE_LIST_DATA = "state:LIST_DATA"

    }

    val danmakuListenerCallback: IDanmakuListenerCallback = DanmakuListenerCallbackImpl()

    val service: MainServiceController =
        MainServiceController(this)
    private val connectivityHelper: ConnectivityAvailabilityHelper =
        ConnectivityAvailabilityHelper(this)
    private var online: Int = 0
    private var danmakuFilter: DanmakuFilter = DanmakuFilter.fromSettings()

    private lateinit var autoScrollHelper: AutoScrollHelper
    private val messageAdapter: MessageListAdapter =
        MessageListAdapter(onItemAdded = {
            if (!isFinishing && autoScrollHelper.autoScrollEnabled) {
                recyclerView.smoothScrollToPosition(it.itemCount)
            }
        })

    private lateinit var toolbarView: View
    private val avatarView by lazy { toolbarView.findViewById<ImageView>(R.id.avatarView) }
    private val usernameView by lazy { toolbarView.findViewById<TextView>(R.id.usernameView) }
    private val statusView by lazy { toolbarView.findViewById<TextView>(R.id.statusView) }

    private val noConnectionsDialogListener = object : NoConnectionsDialogListener {
        override fun onIgnore() {
            lifecycleScope.launchWhenResumed {
                connectToCurrentSubscription()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Settings.introduced) {
            val intent = Intent(this, IntroActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
            return
        }

        setContentView(R.layout.main_activity)

        // Make status bar and nav bar transparent
        setWindowFlags()

        // Set up toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.also {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayShowCustomEnabled(true)
        }
        toolbarView = LayoutInflater.from(toolbar.context)
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
            recyclerView.updatePadding(bottom = bottom - top
                    + resources.getDimensionPixelSize(R.dimen.main_list_bottom_padding_extra))
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
        drawerLayout.addDrawerListener(MainDrawerListener(this))
        TooltipCompat.setTooltipText(fab, getString(R.string.action_open_a_floating_window))
        connectButton.setOnClickListener(this::onConnectButtonClick)
        setFilterButton.onClick {
            FilterSimpleMenuDialogFragment().show(supportFragmentManager, "filter_simple_menu")
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
            messageAdapter.addSystemMessage(getString(R.string.main_welcome_message))
            messageAdapter.addSystemMessage(getString(R.string.main_thanks_message))
        } else {
            online = savedInstanceState.getInt(STATE_ONLINE)
            savedInstanceState.getParcelableArrayList<Parcelable>(STATE_LIST_DATA)?.let {
                messageAdapter.list.clear()
                messageAdapter.list.addAll(it)
            }
        }

        setGestureExclusionEnabled(!drawerLayout.isDrawerOpen(GravityCompat.START))
        updateAvatarAndNameViews()
        updateStatusViews()

        service.register()

        eventsHelper.registerListeners(this, noConnectionsDialogListener)

        val lastVersionCode = Settings.lastVersionCode
        if ((packageVersionCode ?: 0) > lastVersionCode) {
            SuccessfullyUpdatedDialogFragment()
                .show(supportFragmentManager, "updated_tip")
            Settings.updateVersionCode(this)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        try {
            setGestureExclusionEnabled(!drawerLayout.isDrawerOpen(GravityCompat.START))
            setWindowFlags()
        } catch (ignored: Exception) {
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_ONLINE, online)
        outState.putParcelableArrayList(STATE_LIST_DATA, ArrayList(messageAdapter.list))
    }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            else -> super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        service.unregister()
        eventsHelper.unregisterListeners(this, noConnectionsDialogListener)
    }

    override fun onResume() {
        super.onResume()
        connectivityHelper.register()
        ShortcutsUtils.requestUpdateShortcuts(this)
    }

    override fun onPause() {
        super.onPause()
        connectivityHelper.unregister()
        ShortcutsUtils.requestUpdateShortcuts(this)
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
                launchWhenStarted {
                    val current = database.subscriptions().findSelected()
                    if (current != null) {
                        startActivity(IntentUtils.openBilibiliLive(
                            this@MainActivity, current.roomId))
                    }
                }
                true
            }
            R.id.action_room_info -> {
                launchWhenResumed {
                    val current = database.subscriptions().findSelected()
                    if (current != null) {
                        RoomInfoDialogFragment.newInstance(current.roomId)
                            .show(supportFragmentManager, "room_info")
                    }
                }
                true
            }
            R.id.action_add_to_home -> {
                launchWhenResumed {
                    val current = database.subscriptions().findSelected()
                    if (current != null) {
                        if (!ShortcutsUtils.requestPinSubscription(this@MainActivity, current)) {
                            Toast.makeText(this@MainActivity,
                                R.string.toast_failed_to_add_to_home, Toast.LENGTH_LONG).show()
                        }
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSubscriptionChange(current: Subscription?) {
        drawerLayout.closeDrawer(GravityCompat.START)
        updateAvatarAndNameViews()
        launchWhenStarted {
            if (current != null) {
                val needReconnect = withContext(IO) {
                    service.isConnected && service.roomId != current.roomId
                }
                if (needReconnect) {
                    service.connectRoom(current.roomId)
                }
            } else {
                if (service.isConnected()) {
                    service.disconnect()
                    updateStatusViews()
                }
            }
        }
    }

    override fun setWindowFlags(lightNavBar: Boolean?, hideNavigation: Boolean?) {
        super.setWindowFlags(
            lightNavBar ?: drawerLayout.isDrawerOpen(GravityCompat.START),
            hideNavigation)
    }

    override fun onSettingsChanged() {
        danmakuFilter = DanmakuFilter.fromSettings()
    }

    fun setGestureExclusionEnabled(enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (enabled) {
                val screenHeight = windowManager.defaultDisplay.screenHeight
                val height = resources.getDimensionPixelSize(R.dimen.max_gesture_exclusion_height)
                val width = resources.getDimensionPixelSize(R.dimen.drawer_layout_start_area_width)
                drawerLayout.systemGestureExclusionRects = listOf(
                    Rect(0, (screenHeight - height) / 2, width, (screenHeight + height) / 2)
                )
            } else {
                drawerLayout.systemGestureExclusionRects = emptyList()
            }
        }
    }

    private fun onFabClick(view: View) {
        launchWhenResumed {
            val activity = this@MainActivity
            if (!service.isConnected()) {
                Toast.makeText(
                    activity,
                    R.string.toast_connect_room_before_opening_float,
                    Toast.LENGTH_SHORT
                ).show()
                return@launchWhenResumed
            }
            if (!WindowUtils.canDrawOverlays(activity)) {
                showOverlayPermissionDialog()
                return@launchWhenResumed
            }
            askShowFloatingWindow()
        }
    }

    private fun askShowFloatingWindow() = launchWhenResumed {
        if (service.isFloatingShowing()) {
            Toast.makeText(this@MainActivity,
                R.string.toast_floating_is_showing, Toast.LENGTH_SHORT).show()
            // TODO Highlight floating window animation
            return@launchWhenResumed
        }
        showConfirmShowFloatingDialog()
    }

    private fun onConnectButtonClick(view: View) {
        launchWhenResumed {
            if (service.isConnected()) {
                try {
                    service.disconnect()
                } catch (e: Exception) {
                    e.printStackTrace()
                    updateStatusViews()
                }
            } else {
                if (!connectivityHelper.isAvailable) {
                    NoConnectionsDialogFragment.show(supportFragmentManager)
                    return@launchWhenResumed
                }
                connectToCurrentSubscription()
            }
        }
    }

    private suspend fun connectToCurrentSubscription() = withContext(IO) {
        val current = database.subscriptions().getAll().firstOrNull { it.selected }
        if (current != null) {
            service.connectRoom(current.roomId)
        } else {
            launchWhenResumed { showNoStreamerSelectedDialog() }
        }
    }

    private fun updateAvatarAndNameViews() = launchWhenResumed {
        val cur = database.subscriptions().findSelected()
        if (cur != null) {
            usernameView.text = cur.username
            avatarView.avatarUrl = cur.avatar
        } else {
            usernameView.setText(R.string.no_streamer_selected_title)
            Picasso.get().cancelRequest(avatarView)
            avatarView.setImageResource(R.drawable.avatar_placeholder_empty)
        }
    }

    fun updateStatusViews() = launchWhenResumed {
        if (service.isConnected()) {
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

    override fun onStartDanmakuContextMenu(item: BiliChatDanmaku) {
        launchWhenResumed {
            DanmakuContextMenuDialogFragment.newInstance(item)
                .show(supportFragmentManager, "danmaku_context_menu")
        }
    }

    override fun onConfirmBlockText(item: BiliChatDanmaku) {
        MainConfirmBlockTextDialogFragment.show(this, item)
    }

    override fun onConfirmBlockUser(item: BiliChatDanmaku) {
        launchWhenResumed {
            val info = UserApi.getSpaceInfo(item.senderUid)
            if (info.code == 0) {
                MainConfirmBlockUserDialogFragment.show(this@MainActivity, item, info)
            } else {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.toast_cannot_find_user_by_uid_format, item.senderUid),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onBlockText(item: BiliChatDanmaku, blockRule: BlockedTextRule) {
        lifecycleScope.launch {
            onHideDanmaku(item)
            val patterns = Settings.blockedTextPatterns.toMutableList()
            patterns.add(blockRule)
            Settings.commit {
                blockedTextPatterns = patterns
            }
        }
    }

    override fun onBlockUser(item: BiliChatDanmaku, blockUser: BlockedUserRule) {
        lifecycleScope.launch {
            messageAdapter.removeDanmakuByUid(blockUser.uid)
            database.blockedUsers().add(blockUser)
            Settings.notifyChanged(this@MainActivity)
        }
    }

    override fun onHideDanmaku(item: BiliChatDanmaku) {
        messageAdapter.removeDanmaku(item)
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
            lifecycleScope.launch {
                if (withContext(IO) { danmakuFilter(msg) }) {
                    messageAdapter.addDanmaku(msg)
                }
            }
        }

        override fun onHeartbeat(online: Int) {
            this@MainActivity.online = online
            updateStatusViews()
        }

        override fun onConnectFailed(reason: Int) {
            showFailedConnectDialog()
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

}