package moe.feng.danmaqua.ui.main

import android.os.Bundle
import androidx.core.os.bundleOf
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.MainDanmakuContextMenuListener
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.BlockedTextRule
import moe.feng.danmaqua.ui.MainActivity
import moe.feng.danmaqua.ui.common.dialog.ConfirmBlockTextDialogFragment
import androidx.content.eventsHelper

class MainConfirmBlockTextDialogFragment : ConfirmBlockTextDialogFragment() {

    companion object {

        const val ARGS_DANMAKU = "args:DANMAKU"

        fun show(mainActivity: MainActivity, item: BiliChatDanmaku) {
            val fragment = MainConfirmBlockTextDialogFragment()
            fragment.arguments = bundleOf(
                ARGS_DANMAKU to item,
                ARGS_INITIAL_VALUE to BlockedTextRule(item.text))
            fragment.show(mainActivity.supportFragmentManager, "main_confirm_block_text")
        }

    }

    override val titleResourceId: Int = R.string.action_add_rule

    private lateinit var danmaku: BiliChatDanmaku

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        danmaku = requireArguments().getParcelable(ARGS_DANMAKU)!!
    }

    override fun onBlockText(rule: BlockedTextRule) {
        context?.eventsHelper?.of<MainDanmakuContextMenuListener>()?.onBlockText(danmaku, rule)
    }

}