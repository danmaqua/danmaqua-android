package moe.feng.danmaqua.ui.list

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.drakeet.multitype.MultiTypeAdapter
import moe.feng.danmaqua.model.BiliChatDanmaku

class MessageListAdapter(
    val list: MutableList<Parcelable> = mutableListOf(),
    val onItemAdded: (MessageListAdapter) -> Unit = {}
) : MultiTypeAdapter(items = list) {

    private val listLock: Any = object {}

    init {
        register(MainDanmakuItemViewDelegate())
        register(SystemMessageItemViewDelegate())
    }

    fun addDanmaku(danmaku: BiliChatDanmaku) {
        synchronized(listLock) {
            list += danmaku
            notifyItemInserted(list.size - 1)
        }

        onItemAdded(this)
    }

    fun addSystemMessage(text: String) {
        synchronized(listLock) {
            list += SystemMessageItemViewDelegate.Item(text)
            notifyItemInserted(list.size - 1)
        }

        onItemAdded(this)
    }

    fun removeDanmaku(danmaku: BiliChatDanmaku) {
        synchronized(listLock) {
            val index = list.indexOf(danmaku)
            if (index != -1) {
                list -= danmaku
                notifyItemRemoved(index)
            }
        }
    }

    fun removeDanmakuByUid(uid: Long) {
        synchronized(listLock) {
            val oldList = list.toList()
            list.removeAll { it is BiliChatDanmaku && it.senderUid == uid }
            DiffUtil.calculateDiff(ListDiffCallback(oldList, list))
                .dispatchUpdatesTo(this)
        }
    }

    private class ListDiffCallback(val oldItems: List<Parcelable>,
                                   val newItems: List<Parcelable>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areItemsTheSame(oldItemPosition, newItemPosition)
        }

    }

}