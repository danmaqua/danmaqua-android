package moe.feng.danmaqua.ui

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import moe.feng.danmaqua.data.DanmaquaDB
import moe.feng.danmaqua.util.ResourcesUtils

abstract class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    val defaultPreferences: SharedPreferences get() = MMKV.defaultMMKV()

    val database: DanmaquaDB get() = DanmaquaDB.instance

    var lightNavBar: Boolean = false
    var hideNavigation: Boolean = true

    open fun setWindowFlags(lightNavBar: Boolean? = null, hideNavigation: Boolean? = null) {
        if (lightNavBar != null) {
            this.lightNavBar = lightNavBar
        }
        if (hideNavigation != null) {
            this.hideNavigation = hideNavigation
        }

        var lightNavBarFlag = this.lightNavBar
        var flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if (this.hideNavigation) {
            flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            window.navigationBarColor = Color.TRANSPARENT
        } else {
            window.navigationBarColor = ResourcesUtils.resolveColor(
                theme, android.R.attr.navigationBarColor)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                lightNavBarFlag = true
            }
        }
        if (lightNavBarFlag && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        window.decorView.systemUiVisibility = flags
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.cancel()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}