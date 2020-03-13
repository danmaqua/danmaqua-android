package moe.feng.danmaqua.ui.main

import android.os.Bundle
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.event.MainDanmakuContextMenuListener
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.BlockedUserRule
import moe.feng.danmaqua.model.SpaceInfo
import moe.feng.danmaqua.ui.MainActivity
import moe.feng.danmaqua.ui.common.dialog.ConfirmBlockUserDialogFragment
import androidx.content.eventsHelper

class MainConfirmBlockUserDialogFragment : ConfirmBlockUserDialogFragment() {

    companion object {

        const val ARGS_DANMAKU = "args:DANMAKU"

        fun show(mainActivity: MainActivity, item: BiliChatDanmaku, spaceInfo: SpaceInfo) {
            val fragment = MainConfirmBlockUserDialogFragment()
            fragment.arguments = getArguments(
                spaceInfo.data.uid,
                spaceInfo.data.name,
                spaceInfo.data.face
            )
            fragment.requireArguments().putParcelable(ARGS_DANMAKU, item)
            fragment.show(mainActivity.supportFragmentManager, "main_confirm_block_user")
        }

    }

    private lateinit var danmaku: BiliChatDanmaku

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        danmaku = requireArguments().getParcelable(ARGS_DANMAKU)!!
    }

    override suspend fun onConfirmBlock(blockRule: BlockedUserRule) {
        context?.eventsHelper?.of<MainDanmakuContextMenuListener>()?.onBlockUser(danmaku, blockRule)
    }

}