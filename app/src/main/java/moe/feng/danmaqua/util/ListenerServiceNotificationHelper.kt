package moe.feng.danmaqua.util

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.Danmaqua.EXTRA_ACTION
import moe.feng.danmaqua.Danmaqua.NOTI_CHANNEL_ID_STATUS
import moe.feng.danmaqua.Danmaqua.NOTI_ID_LISTENER_STATUS
import moe.feng.danmaqua.Danmaqua.PENDING_INTENT_REQUEST_RECONNECT
import moe.feng.danmaqua.Danmaqua.PENDING_INTENT_REQUEST_STOP
import moe.feng.danmaqua.R
import moe.feng.danmaqua.data.DanmaquaDB
import moe.feng.danmaqua.service.DanmakuListenerService
import moe.feng.danmaqua.service.DanmakuListenerService.Companion.ACTION_RECONNECT
import moe.feng.danmaqua.service.DanmakuListenerService.Companion.ACTION_STOP
import moe.feng.danmaqua.ui.MainActivity

class ListenerServiceNotificationHelper(private val service: DanmakuListenerService) {

    private val notificationManager by lazy { service.getSystemService<NotificationManager>()!! }

    private lateinit var notification: Notification
    private val notificationBuilder: NotificationCompat.Builder =
        NotificationCompat.Builder(service, NOTI_CHANNEL_ID_STATUS)

    private val enterIntent by lazy { Intent(service, MainActivity::class.java) }
    private val stopIntent by lazy {
        Intent(service, DanmakuListenerService::class.java)
            .putExtra(EXTRA_ACTION, ACTION_STOP)
    }
    private val reconnectIntent by lazy {
        Intent(service, DanmakuListenerService::class.java)
            .putExtra(EXTRA_ACTION, ACTION_RECONNECT)
    }

    private val enterPendingIntent by lazy {
        PendingIntent.getActivity(
            service,
            Danmaqua.PENDING_INTENT_REQUEST_ENTER_MAIN,
            enterIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }
    private val stopPendingIntent by lazy {
        PendingIntent.getService(
            service,
            PENDING_INTENT_REQUEST_STOP,
            stopIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }
    private val reconnectPendingIntent by lazy {
        PendingIntent.getService(
            service,
            PENDING_INTENT_REQUEST_RECONNECT,
            reconnectIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    private fun getString(@StringRes res: Int): String {
        return service.getString(res)
    }

    private fun getString(@StringRes res: Int, vararg args: Any?): String {
        return service.getString(res, *args)
    }

    fun onCreate() {
        with (notificationBuilder) {
            color = service.getColor(R.color.colorPrimary)
            setSmallIcon(R.drawable.ic_noti_subtitles_24)
            setContentTitle(getString(R.string.listener_service_noti_title))
            setContentText(getString(R.string.listener_service_noti_text_no_connected))
            setOngoing(true)
            setShowWhen(false)
            setContentIntent(enterPendingIntent)
            setNotificationActions()
        }
        notification = notificationBuilder.build()
    }

    fun startForegroundForService() {
        service.startForeground(NOTI_ID_LISTENER_STATUS, notification)
    }

    fun buildAndNotify() {
        notification = notificationBuilder.build()
        notificationManager.notify(NOTI_ID_LISTENER_STATUS, notification)
    }

    fun showConnectedNotification(roomId: Long) {
        service.launch {
            val current = DanmaquaDB.instance.subscriptions().findByRoomId(roomId)
            val username = current?.username ?: roomId.toString()

            notificationBuilder.setLargeIcon(null)
            notificationBuilder.setStyle(null)
            notificationBuilder.setContentText(getString(
                R.string.listener_service_noti_text_connected,
                username,
                roomId
            ))
            setNotificationActions(showReconnect = false)
            buildAndNotify()

            // Lazy load avatar
            if (current != null) {
                try {
                    val avatarBitmap = withContext(Dispatchers.IO) {
                        Picasso.get().load(current.avatar).get()?.let {
                            BitmapUtils.circular(it)
                        }
                    }
                    if (avatarBitmap != null) {
                        notificationBuilder.setLargeIcon(avatarBitmap)
                        buildAndNotify()
                    }
                } catch (e: Exception) {

                }
            }
        }
    }

    fun showDisconnectedNotification(lastConnectedRoom: Long?) {
        service.launch {
            val text = lastConnectedRoom?.let {
                val current = DanmaquaDB.instance.subscriptions().findByRoomId(it)
                val username = current?.username ?: it.toString()
                getString(R.string.listener_service_noti_disconnected_text_format, username, it)
            } ?: getString(R.string.listener_service_noti_text_no_connected)
            notificationBuilder.setLargeIcon(null)
            notificationBuilder.setStyle(NotificationCompat.BigTextStyle().bigText(text))
            setNotificationActions(showReconnect = lastConnectedRoom != null)
            buildAndNotify()
        }
    }

    fun cancel() {
        notificationManager.cancel(NOTI_ID_LISTENER_STATUS)
    }

    @SuppressLint("RestrictedApi")
    fun setNotificationActions(showReconnect: Boolean = false) {
        with (notificationBuilder) {
            mActions.clear()

            addAction(
                R.drawable.ic_noti_action_stop_24,
                getString(R.string.listener_service_noti_stop),
                stopPendingIntent
            )

            if (showReconnect) {
                addAction(
                    R.drawable.ic_noti_refresh_24,
                    getString(R.string.listener_service_noti_reconnect),
                    reconnectPendingIntent
                )
            }
        }
    }

}