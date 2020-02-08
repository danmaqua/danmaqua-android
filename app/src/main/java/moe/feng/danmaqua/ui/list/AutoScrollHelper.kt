package moe.feng.danmaqua.ui.list

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AutoScrollHelper(
    val recyclerView: RecyclerView,
    var autoScrollEnabled: Boolean = true
) {

    companion object {

        fun create(rv: RecyclerView, autoScrollEnabled: Boolean = true): AutoScrollHelper {
            val helper = AutoScrollHelper(rv, autoScrollEnabled)
            helper.registerListener()
            return helper
        }

    }

    private val listener: Listener = Listener()

    fun registerListener() {
        recyclerView.addOnScrollListener(listener)
    }

    fun unregisterListener() {
        recyclerView.removeOnScrollListener(listener)
    }

    private inner class Listener : RecyclerView.OnScrollListener() {

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPos = layoutManager.findLastCompletelyVisibleItemPosition()
                    val itemCount = layoutManager.itemCount
                    if (lastPos == itemCount - 1) {
                        autoScrollEnabled = true
                    }
                }
                RecyclerView.SCROLL_STATE_DRAGGING -> {
                    autoScrollEnabled = false
                }
            }
        }

    }

}