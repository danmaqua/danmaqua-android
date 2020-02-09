package moe.feng.danmaqua.ui.dialog

import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseDialogFragment : DialogFragment(), CoroutineScope by MainScope() {

    override fun onDestroy() {
        super.onDestroy()
        this.cancel()
    }

}