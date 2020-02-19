package moe.feng.danmaqua.ui.main

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.IDanmakuListenerService
import moe.feng.danmaqua.service.DanmakuListenerService
import moe.feng.danmaqua.ui.MainActivity
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MainServiceController(private val mainActivity: MainActivity) {

    private var service: IDanmakuListenerService? = null
    private var serviceConnection: ServiceConnection? = null

    val isConnected: Boolean get() = service?.isConnected == true
    val roomId: Long? get() = service?.roomId

    fun register() {
        checkStatus()
    }

    fun unregister() {
        service?.unregisterCallback(mainActivity.danmakuListenerCallback)
        if (service?.isConnected != true) {
            stop()
        }
        serviceConnection?.let {
            try {
                mainActivity.unbindService(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun checkStatus() {
        val intent = Intent(mainActivity, DanmakuListenerService::class.java)
        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                if (binder == null) {
                    return
                }
                val service = IDanmakuListenerService.Stub.asInterface(binder)
                if (service?.isConnected == true) {
                    bind()
                }
                mainActivity.unbindService(this)
            }

            override fun onServiceDisconnected(name: ComponentName?) {}
        }
        if (!mainActivity.bindService(intent, connection, Service.BIND_AUTO_CREATE)) {
            mainActivity.unbindService(connection)
        }
    }

    fun startForeground() {
        val intent = Intent(mainActivity, DanmakuListenerService::class.java)
        intent.putExtra(Danmaqua.EXTRA_ACTION, DanmakuListenerService.ACTION_START)
        ContextCompat.startForegroundService(mainActivity, intent)
    }

    fun stop() {
        val intent = Intent(mainActivity, DanmakuListenerService::class.java)
        intent.putExtra(Danmaqua.EXTRA_ACTION, DanmakuListenerService.ACTION_STOP)

        try {
            mainActivity.stopService(intent)
            serviceConnection?.let {
                mainActivity.unbindService(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        service = null
        serviceConnection = null
    }

    fun bind(onConnected: (IDanmakuListenerService) -> Unit = {}) {
        val intent = Intent(mainActivity, DanmakuListenerService::class.java)

        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
                if (binder == null) {
                    return
                }
                service = IDanmakuListenerService.Stub.asInterface(binder).also {
                    it.requestHeartbeat()
                    it.registerCallback(mainActivity.danmakuListenerCallback, false)
                    if (it.isConnected) {
                        mainActivity.updateStatusViews()
                    }
                    onConnected(it)
                }
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                service = null
                mainActivity.updateStatusViews()
            }
        }
        this.serviceConnection = connection
        try {
            if (!mainActivity.bindService(intent, connection, 0)) {
                startForeground()
            }
        } catch (ignored: Exception) {

        }
    }

    private suspend fun ensureService() = withContext(Dispatchers.IO) {
        service ?: run {
            startForeground()
            service = suspendCoroutine<IDanmakuListenerService> { c ->
                bind { c.resume(it) }
            }
            return@run service!!
        }
    }

    suspend fun connectRoom(roomId: Long) = withContext(Dispatchers.IO) {
        try {
            ensureService().connect(roomId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        try {
            service?.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun showFloatingWindow() = withContext(Dispatchers.IO) {
        try {
            ensureService().showFloating()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun isConnected(): Boolean = withContext(Dispatchers.IO) {
        service?.isConnected == true
    }

    suspend fun isFloatingShowing(): Boolean = withContext(Dispatchers.IO) {
        service?.isFloatingShowing == true
    }

}