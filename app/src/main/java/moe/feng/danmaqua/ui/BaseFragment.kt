package moe.feng.danmaqua.ui

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import moe.feng.danmaqua.data.DanmaquaDB

abstract class BaseFragment : Fragment(), CoroutineScope by MainScope() {

    val database: DanmaquaDB get() = DanmaquaDB.instance

    override fun onDestroy() {
        super.onDestroy()
        this.cancel()
    }

}