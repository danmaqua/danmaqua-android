package moe.feng.danmaqua.ui.list

import android.os.Parcelable
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import com.drakeet.multitype.MultiTypeAdapter
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.util.ext.TAG

class MessageListAdapter(
    val list: MutableList<Parcelable> = mutableListOf(),
    val onItemAdded: (MessageListAdapter) -> Unit = {}
) : MultiTypeAdapter(items = list) {

    companion object {

        const val MAX_ITEMS_COUNT = 100

    }

    private val listLock: Any = object {}

    init {
        register(MainDanmakuItemViewDelegate())
        register(SystemMessageItemViewDelegate())
    }

    private fun modifyListAndDispatchUpdate(block: () -> Unit) {
        val oldList = list.toList()
        block()
        DiffUtil.calculateDiff(ListDiffCallback(oldList, list))
            .dispatchUpdatesTo(this)
    }

    fun addDanmaku(danmaku: BiliChatDanmaku) {
        synchronized(listLock) {
            modifyListAndDispatchUpdate {
                list += danmaku
                if (list.size > MAX_ITEMS_COUNT) {
                    list.removeAt(0)
                }
            }
        }

        onItemAdded(this)
    }

    fun addSystemMessage(text: String) {
        synchronized(listLock) {
            modifyListAndDispatchUpdate {
                list += SystemMessageItemViewDelegate.Item(text)
                if (list.size > MAX_ITEMS_COUNT) {
                    list.removeAt(0)
                }
            }
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
            modifyListAndDispatchUpdate {
                list.removeAll { it is BiliChatDanmaku && it.senderUid == uid }
            }
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