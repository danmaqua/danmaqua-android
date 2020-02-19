package moe.feng.danmaqua.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.core.content.getSystemService

class ConnectivityAvailabilityHelper(private val context: Context) {

    private var isConnectivityAvailable: Boolean = false
    private val connectivityCallback: ConnectivityCallback = ConnectivityCallback()

    private val connectivityManager by lazy { context.getSystemService<ConnectivityManager>() }

    val isAvailable: Boolean get() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return isConnectivityAvailable
        } else {
            return connectivityManager?.isDefaultNetworkActive == true
        }
    }

    fun register() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager?.registerDefaultNetworkCallback(connectivityCallback)
        }
    }

    fun unregister() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager?.unregisterNetworkCallback(connectivityCallback)
        }
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