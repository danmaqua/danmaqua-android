package moe.feng.danmaqua.ui

import android.app.Activity
import android.content.Context
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val title = getActivityTitle(context)
        if (title != null) {
            val activity = if (context is Activity) context else this.activity
            activity?.title = title
        }
    }

    open fun getActivityTitle(context: Context): String? {
        return null
    }

}