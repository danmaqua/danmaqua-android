package moe.feng.danmaqua.ui.floating

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.PixelFormat
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.getSystemService
import kotlinx.coroutines.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.util.ext.TAG
import java.lang.Exception

@SuppressLint("ClickableViewAccessibility")
class FloatingWindowHolder(val rootView: View)
    : ContextWrapper(rootView.context), CoroutineScope by MainScope() {

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

    val windowLayoutParams: WindowManager.LayoutParams = WindowManager.LayoutParams()

    var isAdded: Boolean = false

    val backgroundView: View = rootView.findViewById(R.id.floatingBackground)
    val contentView: View = rootView.findViewById(R.id.floatingContent)
    val captionView: TextView = rootView.findViewById(R.id.captionView)

    val backgroundViewParams: FrameLayout.LayoutParams =
        backgroundView.layoutParams as FrameLayout.LayoutParams

    init {
        with (windowLayoutParams) {
            type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            }
            width = WindowManager.LayoutParams.WRAP_CONTENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            format = PixelFormat.TRANSPARENT
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        }

        contentView.viewTreeObserver.addOnGlobalLayoutListener {
            backgroundViewParams.width = contentView.measuredWidth
            backgroundViewParams.height = contentView.measuredHeight
            backgroundView.requestLayout()
        }
        contentView.setOnTouchListener(WindowOnTouchListener())

        captionView.text = "Test"
    }

    fun addToWindowManager() {
        if (isAdded) {
            Log.e(TAG, "FloatingWindow has been added to WindowManager.")
            return
        }
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

    fun updateViewParams() {
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

    fun addDanmaku(msg: BiliChatDanmaku) {
        captionView.text = msg.text
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
                            dragStartJob = launch {
                                delay(longPressDuration)
                                synchronized(calcLock) {
                                    if (!isTouchOutOfView) {
                                        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                        initialX = windowLayoutParams.x
                                        initialY = windowLayoutParams.y
                                        initialTouchX = currentTouchX
                                        initialTouchY = currentTouchY
                                        isDragging = true
                                    }
                                }
                            }
                        }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        isDragging = false
                        isTouchOutOfView = false
                        dragStartJob?.cancel()
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        currentTouchX = event.rawX
                        currentTouchY = event.rawY
                        if (isDragging) {
                            windowLayoutParams.let {
                                it.x = initialX + (currentTouchX - initialTouchX).toInt()
                                it.y = initialY + (currentTouchY - initialTouchY).toInt()
                            }
                            updateViewParams()
                        } else {
                            if (event.x < 0 || event.y < 0 ||
                                event.x > contentView.measuredWidth ||
                                event.y > contentView.measuredHeight) {
                                isTouchOutOfView = true
                            }
                        }
                        return true
                    }
                }
                return false
            }
        }

    }

}