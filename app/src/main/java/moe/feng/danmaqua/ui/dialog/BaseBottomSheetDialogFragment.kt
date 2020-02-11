package moe.feng.danmaqua.ui.dialog

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseBottomSheetDialogFragment
    : BottomSheetDialogFragment(), CoroutineScope by MainScope() {

    override fun onDestroy() {
        super.onDestroy()
        this.cancel()
    }

}