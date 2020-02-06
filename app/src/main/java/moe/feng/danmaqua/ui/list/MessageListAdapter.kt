package moe.feng.danmaqua.ui.list

import com.drakeet.multitype.MultiTypeAdapter
import moe.feng.danmaqua.model.BiliChatDanmaku

class MessageListAdapter(
    val list: MutableList<Any> = mutableListOf(),
    val onItemAdded: (MessageListAdapter) -> Unit = {}
) : MultiTypeAdapter(items = list) {

    init {
        register(SimpleDanmakuItemViewDelegate())
        register(SystemMessageItemViewDelegate())
    }

    fun addDanmaku(danmaku: BiliChatDanmaku) {
        list += danmaku
        notifyItemInserted(list.size - 1)

        onItemAdded(this)
    }

    fun addSystemMessage(text: String) {
        list += SystemMessageItemViewDelegate.Item(text)
        notifyItemInserted(list.size - 1)

        onItemAdded(this)
    }

}