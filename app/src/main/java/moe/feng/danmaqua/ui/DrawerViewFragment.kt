package moe.feng.danmaqua.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
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
import moe.feng.danmaqua.ui.list.RaisedViewScrollListener
import moe.feng.danmaqua.ui.list.SubscriptionAddItemViewDelegate
import moe.feng.danmaqua.ui.list.SubscriptionItemViewDelegate
import moe.feng.danmaqua.ui.settings.MainSettingsFragment

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
        val context = view.context

        view.setOnApplyWindowInsetsListener { _, insets ->
            drawerList.updatePadding(top = insets.systemWindowInsetTop +
                    resources.getDimensionPixelSize(R.dimen.subscription_list_default_padding_top))
            statusBarBackground.updateLayoutParams {
                height = insets.systemWindowInsetTop
            }
            bottomBar.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }

        drawerList.adapter = drawerListAdapter
        drawerList.addOnScrollListener(RaisedViewScrollListener(
            bottomBar,
            context.resources.getDimension(R.dimen.subscription_list_raised_view_elevation),
            0F,
            context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        ))

        val appName = getString(R.string.app_name)
        var versionName = "Unknown"
        var versionCode = 0
        try {
            val packInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            versionName = packInfo.versionName
            versionCode = packInfo.versionCode
        } catch (e: Exception) {
            e.printStackTrace()
        }
        appVersionView.text = getString(R.string.app_name_with_version_text_format,
            appName, versionName, versionCode)

        settingsButton.setOnClickListener {
            PreferenceActivity.launch(requireActivity(), MainSettingsFragment.ACTION)
        }

        updateAdapterData(scrollToSelection = true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_NEW_SUBSCRIPTION -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val subscription = data.getParcelableExtra<Subscription>(EXTRA_DATA) ?: return
                    launch {
                        val dao = database.subscriptions()
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

    private fun updateAdapterData(
        updateItems: List<Subscription>? = null,
        scrollToSelection: Boolean = false
    ) = launch {
        val oldItems = drawerListAdapter.items
        val newItems = withContext(Dispatchers.IO) {
            val result = mutableListOf<Any>()
            result.addAll(updateItems ?: database.subscriptions().getAll())
            result += SubscriptionAddItemViewDelegate.Item
            result
        }
        val callback = DrawerListDiffCallback(oldItems, newItems)
        val diffResult = DiffUtil.calculateDiff(callback)
        drawerListAdapter.items = newItems
        diffResult.dispatchUpdatesTo(drawerListAdapter)
        if (scrollToSelection) {
            for ((index, item) in newItems.withIndex()) {
                if ((item as? Subscription)?.selected == true) {
                    drawerList.scrollToPosition(index)
                    break
                }
            }
        }
    }

    private inner class DrawerListAdapter : MultiTypeAdapter(),
        SubscriptionItemViewDelegate.Callback, SubscriptionAddItemViewDelegate.Callback {

        val subscriptionItemDelegate = SubscriptionItemViewDelegate(this)
        val subscriptionAddDelegate = SubscriptionAddItemViewDelegate(this)

        init {
            register(subscriptionItemDelegate)
            register(subscriptionAddDelegate)
        }

        override fun onSubscriptionItemClick(item: Subscription) {
            launch {
                val dao = database.subscriptions()
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