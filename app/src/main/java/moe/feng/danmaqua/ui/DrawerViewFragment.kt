package moe.feng.danmaqua.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.main_drawer_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.Danmaqua.EXTRA_DATA
import moe.feng.danmaqua.DanmaquaApplication
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.ui.list.SubscriptionAddItemViewDelegate
import moe.feng.danmaqua.ui.list.SubscriptionItemViewDelegate
import moe.feng.danmaqua.util.ext.TAG

class DrawerViewFragment : BaseFragment() {

    companion object {

        const val REQUEST_CODE_NEW_SUBSCRIPTION = 10000

    }

    interface Callback {

        fun onSubscriptionChange(current: Subscription)

    }

    var callback: Callback? = null

    private val drawerListAdapter: DrawerListAdapter = DrawerListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_drawer_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        drawerList.adapter = drawerListAdapter

        updateAdapterData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_NEW_SUBSCRIPTION -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val subscription = data.getParcelableExtra<Subscription>(EXTRA_DATA) ?: return
                    launch {
                        val dao = DanmaquaApplication.getDatabase(requireContext()).subscriptions()
                        if (dao.findByUid(subscription.uid) == null) {
                            if (dao.findSelected() == null) {
                                subscription.selected = true
                            }
                            dao.add(subscription)
                            if (subscription.selected) {
                                callback?.onSubscriptionChange(subscription)
                            }
                            updateAdapterData()
                        }
                    }
                }
            }
        }
    }

    private fun updateAdapterData(updateItems: List<Subscription>? = null) = launch {
        val oldItems = drawerListAdapter.items
        val newItems = withContext(Dispatchers.IO) {
            val result = mutableListOf<Any>()
            val db = DanmaquaApplication.getDatabase(requireContext())
            result.addAll(updateItems ?: db.subscriptions().getAll())
            result += SubscriptionAddItemViewDelegate.Item
            result
        }
        val callback = DrawerListDiffCallback(oldItems, newItems)
        val diffResult = DiffUtil.calculateDiff(callback)
        drawerListAdapter.items = newItems
        diffResult.dispatchUpdatesTo(drawerListAdapter)
    }

    private inner class DrawerListAdapter : MultiTypeAdapter(),
        SubscriptionItemViewDelegate.Callback, SubscriptionAddItemViewDelegate.Callback {

        init {
            register(SubscriptionItemViewDelegate(this))
            register(SubscriptionAddItemViewDelegate(this))
        }

        override fun onSubscriptionItemClick(item: Subscription) {
            launch {
                val dao = DanmaquaApplication.getDatabase(requireContext()).subscriptions()
                val items = dao.getAll()
                items.forEach {
                    it.selected = it.uid == item.uid
                    dao.update(it)
                }
                updateAdapterData(items)
                callback?.onSubscriptionChange(item)
            }
        }

        override fun onSubscriptionAddClick() {
            val intent = Intent(requireContext(), NewSubscriptionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            startActivityForResult(intent, REQUEST_CODE_NEW_SUBSCRIPTION)
        }

    }

    private class DrawerListDiffCallback(
        val oldItems: List<Any>,
        val newItems: List<Any>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            if (oldItem is Subscription && newItem is Subscription) {
                return oldItem.uid == newItem.uid
            } else {
                return oldItem == newItem
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            // Subscription implemented Object#equals(other) so we can use == operation to compare.
            return oldItem == newItem
        }

    }

}