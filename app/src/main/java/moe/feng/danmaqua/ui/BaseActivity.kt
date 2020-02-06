package moe.feng.danmaqua.ui

import android.content.SharedPreferences
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import moe.feng.danmaqua.DanmaquaApplication
import moe.feng.danmaqua.data.DanmaquaDB

abstract class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    val defaultPreferences: SharedPreferences get() = MMKV.defaultMMKV()

    val database: DanmaquaDB get() = DanmaquaApplication.getDatabase(this)

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