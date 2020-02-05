package moe.feng.danmaqua.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import moe.feng.danmaqua.Danmaqua.EXTRA_ACTION
import moe.feng.danmaqua.Danmaqua.NOTI_CHANNEL_ID_STATUS
import moe.feng.danmaqua.Danmaqua.NOTI_ID_LISTENER_STATUS
import moe.feng.danmaqua.Danmaqua.PENDING_INTENT_REQUEST_ENTER_MAIN
import moe.feng.danmaqua.Danmaqua.PENDING_INTENT_REQUEST_STOP
import moe.feng.danmaqua.IDanmakuListenerCallback
import moe.feng.danmaqua.IDanmakuListenerService
import moe.feng.danmaqua.R
import moe.feng.danmaqua.api.DanmakuApi
import moe.feng.danmaqua.api.DanmakuListener
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.BiliChatMessage
import moe.feng.danmaqua.ui.MainActivity
import moe.feng.danmaqua.util.ext.TAG
import moe.feng.danmaqua.util.ext.getDanmaquaDatabase
import java.lang.Exception

class DanmakuListenerService :
    Service(), CoroutineScope by MainScope(), DanmakuListener.Callback {

    companion object {

        const val ACTION_START = "start"
        const val ACTION_STOP = "stop"

    }

    private val binder: AidlInterfaceImpl = AidlInterfaceImpl()
    private val notificationManager by lazy { getSystemService<NotificationManager>()!! }

    private var danmakuListener: DanmakuListener? = null
    private val serviceCallbacks: MutableList<CallbackHolder> = mutableListOf()

    private lateinit var notification: Notification
    private val notificationBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(this, NOTI_CHANNEL_ID_STATUS)

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Called onCreate")

        // Initialize status notification
        val enterIntent = Intent(this, MainActivity::class.java)
        val enterPi = PendingIntent.getActivity(
            this,
            PENDING_INTENT_REQUEST_ENTER_MAIN,
            enterIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val stopIntent = Intent(this, DanmakuListenerService::class.java)
        stopIntent.putExtra(EXTRA_ACTION, ACTION_STOP)
        val stopPi = PendingIntent.getService(
            this,
            PENDING_INTENT_REQUEST_STOP,
            stopIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        with (notificationBuilder) {
            setSmallIcon(R.drawable.ic_noti_subtitles_24)
            setContentTitle(getString(R.string.listener_service_noti_title))
            setContentText(getString(R.string.listener_service_noti_text_no_connected))
            setOngoing(true)
            setShowWhen(false)
            setContentIntent(enterPi)
            addAction(
                R.drawable.ic_noti_action_stop_24,
                getString(R.string.listener_service_noti_stop),
                stopPi
            )
        }
        notification = notificationBuilder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Called onDestroy")
        try {
            stopForeground(true)
            notificationManager.cancel(NOTI_ID_LISTENER_STATUS)
        } catch (e: Exception) {

        }
        this.cancel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.getStringExtra(EXTRA_ACTION)
        Log.d(TAG, "onStartCommand: action=$action")
        when (action) {
            ACTION_START -> {
                startForeground(NOTI_ID_LISTENER_STATUS, notification)
            }
            ACTION_STOP -> {
                disconnect()
                stopForeground(true)
                stopSelf()
                return START_NOT_STICKY
            }
        }
        return START_STICKY
    }

    private fun connect(roomId: Long) {
        Log.d(TAG, "connect roomId=$roomId")
        danmakuListener?.close()
        danmakuListener = DanmakuApi.listen(roomId, this)

        // TODO Save local history
    }

    private fun disconnect() {
        Log.d(TAG, "disconnect")
        try {
            danmakuListener?.close()
        } catch (e: Exception) {

        }
    }

    override fun onConnect() {
        danmakuListener?.let {
            launch {
                val current = getDanmaquaDatabase().subscriptions().findByRoomId(it.roomId)
                val username = current?.username ?: it.roomId.toString()

                notificationBuilder.setContentText(getString(
                    R.string.listener_service_noti_text_connected,
                    username,
                    it.roomId
                ))
                notification = notificationBuilder.build()
                notificationManager.notify(NOTI_ID_LISTENER_STATUS, notification)
            }

            for ((callback, _) in serviceCallbacks) {
                callback.onConnect(it.roomId)
            }
        } ?: Log.e(TAG, "DanmakuListener is null but called onConnect.")
    }

    override fun onDisconnect(userReason: Boolean) {
        notificationBuilder.setContentText(
            getString(R.string.listener_service_noti_text_no_connected))
        notification = notificationBuilder.build()
        notificationManager.notify(NOTI_ID_LISTENER_STATUS, notification)

        for ((callback, _) in serviceCallbacks) {
            callback.onDisconnect()
        }
    }

    override fun onHeartbeat(online: Int) {
        for ((callback, _) in serviceCallbacks) {
            callback.onHeartbeat(online)
        }
    }

    override fun onMessage(msg: BiliChatMessage) {
        if (msg !is BiliChatDanmaku) {
            return
        }
        Log.d(TAG, "onMessage: $msg")
        for ((callback, _) in serviceCallbacks) {
            // TODO Implement filter
            callback.onReceiveDanmaku(msg)
        }
    }

    override fun onFailure(t: Throwable) {
        Log.e(TAG, "onFailure", t)
    }

    private inner class AidlInterfaceImpl : IDanmakuListenerService.Stub() {

        override fun connect(roomId: Long) {
            this@DanmakuListenerService.connect(roomId)
        }

        override fun disconnect() {
            this@DanmakuListenerService.disconnect()
        }

        override fun requestHeartbeat() {
            danmakuListener?.requestHeartbeat()
        }

        override fun isConnected(): Boolean {
            return danmakuListener?.isConnected == true
        }

        override fun getRoomId(): Long {
            return danmakuListener?.roomId ?: 0L
        }

        override fun registerCallback(callback: IDanmakuListenerCallback?, filter: Boolean) {
            if (callback == null) {
                return
            }
            val callbackHolder = serviceCallbacks.find { it.callback == callback }
            if (callbackHolder == null) {
                serviceCallbacks += CallbackHolder(callback, filter)
            } else {
                callbackHolder.filter = true
            }
        }

        override fun unregisterCallback(callback: IDanmakuListenerCallback?) {
            if (callback == null) {
                return
            }
            serviceCallbacks.removeAll { it.callback == callback }
        }

    }

    private data class CallbackHolder(
        val callback: IDanmakuListenerCallback,
        var filter: Boolean
    )

}