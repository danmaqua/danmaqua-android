package moe.feng.danmaqua.event

import moe.feng.common.eventshelper.EventsListener
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.BlockedTextRule
import moe.feng.danmaqua.model.BlockedUserRule

@EventsListener
interface MainDanmakuContextMenuListener {

    fun onStartDanmakuContextMenu(item: BiliChatDanmaku)

    fun onConfirmBlockText(item: BiliChatDanmaku)

    fun onConfirmBlockUser(item: BiliChatDanmaku)

    fun onBlockText(item: BiliChatDanmaku, blockRule: BlockedTextRule)

    fun onBlockUser(item: BiliChatDanmaku, blockUser: BlockedUserRule)

    fun onHideDanmaku(item: BiliChatDanmaku)

}