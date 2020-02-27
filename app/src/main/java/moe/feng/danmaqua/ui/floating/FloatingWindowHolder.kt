package moe.feng.danmaqua.ui.floating

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.getSystemService
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.coroutines.*
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.ui.list.AutoScrollHelper
import moe.feng.danmaqua.ui.floating.list.FWDanmakuItemViewDelegate
import moe.feng.danmaqua.ui.floating.list.FWSystemMessageItemViewDelegate
import moe.feng.danmaqua.util.DanmakuFilter
import moe.feng.danmaqua.util.ext.TAG
import moe.feng.danmaqua.util.ext.screenHeight
import moe.feng.danmaqua.util.ext.screenWidth
import moe.feng.danmaqua.util.flattenToString
import java.lang.Exception

@SuppressLint("ClickableViewAccessibility")
class FloatingWindowHolder(
    val rootView: View,
    var onCloseClick: () -> Unit = {},
    var onGetDanmakuFilter: () -> DanmakuFilter = { DanmakuFilter.acceptAll() }
) : ContextWrapper(rootView.context), CoroutineScope by MainScope() {

    companion object {

        fun create(context: Context): FloatingWindowHolder {
            val themedContext = ContextThemeWrapper(
                context, R.style.Theme_MaterialComponents_Dialog)
            val rootView = LayoutInflater.from(themedContext)
                .inflate(R.layout.floating_window_view, null)
            return FloatingWindowHolder(rootView)
        }

    }

    private val windowManager: WindowManager = getSystemService()!!

    val horizontalMargin = resources.getDimensionPixelSize(
        R.dimen.floating_window_horizontal_margin)
    val verticalMargin = resources.getDimensionPixelSize(
        R.dimen.floating_window_vertical_margin)

    val windowLayoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()

    var isAdded: Boolean = false
    var isLandscaped: Boolean = true
    var isExpanded: Boolean
        get() = !normalContent.isVisible
        set(value) {
            if (value) {
                normalContent.isGone = true
                expandedContent.isVisible = true
            } else {
                expandedContent.isGone = true
                normalContent.isVisible = true
            }
        }
    val danmakuList: MutableList<Any> = mutableListOf()
    val danmakuAdapter: MultiTypeAdapter = MultiTypeAdapter(items = danmakuList).also {
        it.register(
            FWDanmakuItemViewDelegate(this)
        )
        it.register(
            FWSystemMessageItemViewDelegate(this)
        )
    }

    var danmakuMaxCount: Int = 30
    var textSize: Int = 14
    var twoLine: Boolean = false
    var backgroundAlpha: Int = 255
    var touchToMoveEnabled: Boolean = true
    val danmakuFilter: DanmakuFilter get() = onGetDanmakuFilter()

    val backgroundView: View = rootView.findViewById(R.id.floatingBackground)
    val contentView: View = rootView.findViewById(R.id.floatingContent)
    val normalContent: View = rootView.findViewById(R.id.normalContent)
    val expandedContent: View = rootView.findViewById(R.id.expandedContent)
    val captionView: TextView = rootView.findViewById(R.id.captionView)
    val expandButton: View = rootView.findViewById(R.id.expandButton)
    val listView: RecyclerView = rootView.findViewById(R.id.danmakuList)
    val collapseButton: View = rootView.findViewById(R.id.collapseButton)
    val closeButton: View = rootView.findViewById(R.id.closeButton)

    val autoScrollHelper: AutoScrollHelper = AutoScrollHelper.create(listView)

    val backgroundViewParams: FrameLayout.LayoutParams =
        backgroundView.layoutParams as FrameLayout.LayoutParams

    val danmakuListLock: Any = object {}

    init {
        with (windowLayoutParams) {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            val screenHeight = windowManager.defaultDisplay.screenHeight
            y = screenHeight / 3 * 2
            gravity = Gravity.TOP or Gravity.START
            format = PixelFormat.TRANSPARENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        }

        contentView.addOnLayoutChangeListener { _, _, top, _, bottom, _, oldTop, _, oldBottom ->
            if ((oldBottom - oldTop) != (bottom - top)) {
                backgroundViewParams.height = bottom - top
                backgroundView.requestLayout()
            }
        }
        contentView.setOnTouchListener(WindowOnTouchListener())

        expandButton.setOnClickListener {
            isExpanded = true
        }
        collapseButton.setOnClickListener {
            isExpanded = false
            autoScrollHelper.autoScrollEnabled = true
        }
        closeButton.setOnClickListener {
            removeFromWindowManager()
            onCloseClick()
        }

        listView.adapter = danmakuAdapter

        loadSettings()
    }

    fun loadSettings() {
        textSize = Settings.floatingTextSize
        twoLine = Settings.floatingTwoLine
        backgroundAlpha = Settings.floatingBackgroundAlpha
        touchToMoveEnabled = Settings.floatingTouchToMove

        captionView.textSize = textSize.toFloat()
        backgroundView.alpha = backgroundAlpha.toFloat() / 255F

        danmakuAdapter.notifyDataSetChanged()
    }

    fun addToWindowManager() {
        if (isAdded) {
            Log.e(TAG, "FloatingWindow has been added to WindowManager.")
            return
        }
        isLandscaped = when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> true
            Configuration.ORIENTATION_PORTRAIT -> false
            else -> true
        }
        isExpanded = false
        autoScrollHelper.autoScrollEnabled = true
        updateViewParamsByOrientation()
        try {
            windowManager.addView(rootView, windowLayoutParams)
            isAdded = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeFromWindowManager() {
        if (!isAdded) {
            Log.e(TAG, "FloatingWindow hasn't been added to WindowManager.")
            return
        }
        try {
            windowManager.removeViewImmediate(rootView)
            isAdded = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateViewParamsToWindowManager() {
        if (!isAdded) {
            Log.e(TAG, "FloatingWindow hasn't been added to WindowManager.")
            return
        }
        try {
            windowManager.updateViewLayout(rootView, windowLayoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateViewParamsByOrientation() {
        Log.d(TAG, "updateViewParamsByOrientation: isLandscaped=$isLandscaped")
        val screenWidth = windowManager.defaultDisplay.screenWidth
        val screenHeight = windowManager.defaultDisplay.screenHeight
        if (!isLandscaped) {
            windowLayoutParams.width = screenWidth - horizontalMargin * 2
            windowLayoutParams.x = horizontalMargin
        } else {
            windowLayoutParams.width = screenWidth / 3 * 2
            windowLayoutParams.x = horizontalMargin
        }
        windowLayoutParams.y = windowLayoutParams.y.coerceAtMost(
            screenHeight - contentView.measuredHeight
        )
        updateViewParamsToWindowManager()
    }

    fun addDanmaku(msg: BiliChatDanmaku) {
        synchronized(danmakuListLock) {
            danmakuList.add(msg)
            if (danmakuList.size > danmakuMaxCount) {
                var removing = danmakuList.size - danmakuMaxCount
                while (removing > 0) {
                    danmakuList.removeAt(0)
                    removing--
                }
            }
        }
        updateDanmakuCaptionViews()
    }

    fun addSystemMessage(msg: String) {
        synchronized(danmakuListLock) {
            danmakuList.add(msg)
            if (danmakuList.size > danmakuMaxCount) {
                var removing = danmakuList.size - danmakuMaxCount
                while (removing > 0) {
                    danmakuList.removeAt(0)
                    removing--
                }
            }
        }
        updateDanmakuCaptionViews()
    }

    private fun updateDanmakuCaptionViews() {
        danmakuAdapter.notifyDataSetChanged()
        if (autoScrollHelper.autoScrollEnabled) {
            listView.scrollToPosition(danmakuList.size - 1)
        }
        captionView.text = if (twoLine && danmakuList.size >= 2) {
            val reversed = danmakuList.asReversed()
            val prevSubtitle = when (val it = reversed[1]) {
                is BiliChatDanmaku -> danmakuFilter.unescapeSubtitle(it)?.flattenToString()
                is String -> it
                else -> it.toString()
            }
            val currSubtitle = when (val it = reversed[0]) {
                is BiliChatDanmaku -> danmakuFilter.unescapeSubtitle(it)?.flattenToString()
                is String -> it
                else -> it.toString()
            }
            buildSpannedString {
                prevSubtitle?.let {
                    append(it)
                    append("\n")
                }
                currSubtitle?.let {
                    bold { append(it) }
                }
            }
        } else {
            danmakuList.lastOrNull()
                ?.let {
                    when (it) {
                        is BiliChatDanmaku -> danmakuFilter.unescapeSubtitle(it)?.flattenToString()
                        is String -> it
                        else -> it.toString()
                    }
                } ?: ""
        }
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        val newState = when (newConfig.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> true
            Configuration.ORIENTATION_PORTRAIT -> false
            else -> true
        }
        if (newState != isLandscaped) {
            isLandscaped = newState
            updateViewParamsByOrientation()
        }
    }

    private inner class WindowOnTouchListener(
        val longPressDuration: Long = ViewConfiguration.getLongPressTimeout().toLong()
    ) : View.OnTouchListener {

        private var isDragging: Boolean = false
        private var isTouchOutOfView: Boolean = false

        private var initialX: Int = 0
        private var initialY: Int = 0
        private var initialTouchX: Float = 0F
        private var initialTouchY: Float = 0F
        private var currentTouchX: Float = 0F
        private var currentTouchY: Float = 0F

        private var dragStartJob: Job? = null

        private val calcLock: Any = object {}

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            synchronized(calcLock) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (!isDragging) {
                            if (touchToMoveEnabled) {
                                v.performHapticFeedback(
                                    HapticFeedbackConstants.CLOCK_TICK)
                                initialX = windowLayoutParams.x
                                initialY = windowLayoutParams.y
                                initialTouchX = event.rawX
                                initialTouchY = event.rawY
                                isDragging = true
                            } else {
                                dragStartJob = launch {
                                    delay(longPressDuration)
                                    synchronized(calcLock) {
                                        if (!isTouchOutOfView) {
                                            v.performHapticFeedback(
                                                HapticFeedbackConstants.LONG_PRESS)
                                            initialX = windowLayoutParams.x
                                            initialY = windowLayoutParams.y
                                            initialTouchX = currentTouchX
                                            initialTouchY = currentTouchY
                                            isDragging = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        isDragging = false
                        isTouchOutOfView = false
                        dragStartJob?.cancel()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        currentTouchX = event.rawX
                        currentTouchY = event.rawY
                        if (isDragging) {
                            windowLayoutParams.let {
                                if (isLandscaped) {
                                    it.x = (initialX + (currentTouchX - initialTouchX).toInt())
                                        .coerceAtLeast(0)
                                }
                                it.y = (initialY + (currentTouchY - initialTouchY).toInt())
                                    .coerceAtLeast(0)
                            }
                            updateViewParamsToWindowManager()
                            return true
                        } else {
                            if (event.x < 0 || event.y < 0 ||
                                event.x > contentView.measuredWidth ||
                                event.y > contentView.measuredHeight) {
                                isTouchOutOfView = true
                            }
                        }
                    }
                }
                return false
            }
        }

    }

}