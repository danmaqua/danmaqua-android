package moe.feng.danmaqua.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.drakeet.multitype.MultiTypeAdapter
import com.google.androidbrowserhelper.trusted.TwaLauncher
import kotlinx.android.synthetic.main.main_drawer_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.Danmaqua.EXTRA_DATA
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.MainDrawerCallback
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.ui.BaseFragment
import moe.feng.danmaqua.ui.ManageSubscriptionActivity
import moe.feng.danmaqua.ui.NewSubscriptionActivity
import moe.feng.danmaqua.ui.PreferenceActivity
import moe.feng.danmaqua.ui.list.RaisedViewScrollListener
import moe.feng.danmaqua.ui.list.SubscriptionAddButtonViewDelegate
import moe.feng.danmaqua.ui.list.SubscriptionItemViewDelegate
import moe.feng.danmaqua.ui.list.SubscriptionManageButtonViewDelegate
import moe.feng.danmaqua.ui.settings.ExperimentSettingsFragment
import moe.feng.danmaqua.ui.settings.MainSettingsFragment
import moe.feng.danmaqua.ui.settings.SupportUsFragment
import moe.feng.danmaqua.util.ext.*

class DrawerViewFragment : BaseFragment() {

    companion object {

        const val REQUEST_CODE_NEW_SUBSCRIPTION = 10000
        const val REQUEST_CODE_MANAGE_SUBSCRIPTION = 10001

    }

    private var developmentTimes: Int = 0

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
        appVersionView.setOnClickListener {
            developmentTimes++
            if (developmentTimes >= 5) {
                developmentTimes = 0
                PreferenceActivity.launch(activity!!, ExperimentSettingsFragment.ACTION)
            }
        }

        settingsButton.setOnClickListener {
            PreferenceActivity
                .launch(requireActivity(), MainSettingsFragment.ACTION)
        }
        helpButton.setOnClickListener {
            TwaLauncher(it.context)
                .launch(getString(R.string.help_url).toUri())
        }
        donateButton.setOnClickListener {
            PreferenceActivity
                .launch(requireActivity(), SupportUsFragment.ACTION)
        }

        updateAdapterData(scrollToSelection = true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_NEW_SUBSCRIPTION -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val subscription = data.getParcelableExtra<Subscription>(EXTRA_DATA) ?: return
                    lifecycleScope.launch {
                        val dao = database.subscriptions()
                        if (dao.findByUid(subscription.uid) == null) {
                            if (dao.findSelected() == null) {
                                subscription.selected = true
                            }
                            dao.add(subscription)
                            if (subscription.selected) {
                                context?.eventsHelper?.of<MainDrawerCallback>()
                                    ?.onSubscriptionChange(subscription)
                            }
                            updateAdapterData()
                        }
                    }
                }
            }
            REQUEST_CODE_MANAGE_SUBSCRIPTION -> {
                if (resultCode == Activity.RESULT_OK) {
                    lifecycleScope.launch {
                        val dao = database.subscriptions()
                        val selectedSubscription = dao.findSelected() ?: dao.getAll().firstOrNull()
                        selectedSubscription?.selected = true
                        selectedSubscription?.let { dao.update(it) }
                        context?.eventsHelper?.of<MainDrawerCallback>()
                            ?.onSubscriptionChange(selectedSubscription)
                        updateAdapterData()
                    }
                }
            }
        }
    }

    private fun updateAdapterData(
        updateItems: List<Subscription>? = null,
        scrollToSelection: Boolean = false
    ) = lifecycleScope.launch {
        val oldItems = drawerListAdapter.items
        val newItems = withContext(Dispatchers.IO) {
            val result = mutableListOf<Any>()
            result.addAll(updateItems ?: database.subscriptions().getAll())
            result += SubscriptionAddButtonViewDelegate.Item
            result += SubscriptionManageButtonViewDelegate.Item
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
        SubscriptionItemViewDelegate.Callback,
        SubscriptionAddButtonViewDelegate.Callback,
        SubscriptionManageButtonViewDelegate.Callback {

        val subscriptionItemDelegate = SubscriptionItemViewDelegate(this)
        val subscriptionAddDelegate = SubscriptionAddButtonViewDelegate(this)
        val subscriptionManageDelegate = SubscriptionManageButtonViewDelegate(this)

        init {
            register(subscriptionItemDelegate)
            register(subscriptionAddDelegate)
            register(subscriptionManageDelegate)
        }

        override fun onSubscriptionItemClick(item: Subscription) {
            lifecycleScope.launch {
                val dao = database.subscriptions()
                val items = dao.getAll()
                items.forEach {
                    it.selected = it.uid == item.uid
                    dao.update(it)
                }
                updateAdapterData(items)
                context?.eventsHelper?.of<MainDrawerCallback>()
                    ?.onSubscriptionChange(item)
            }
        }

        override fun onSubscriptionItemLongClick(item: Subscription) {
            activity?.showAlertDialog {
                titleRes = R.string.unsubscribe_dialog_title
                message = getString(R.string.unsubscribe_dialog_message, item.username)
                yesButton {
                    lifecycleScope.launch {
                        val dao = database.subscriptions()
                        val lastSelected = item.selected
                        dao.delete(item)
                        val items = dao.getAll()
                        if (lastSelected) {
                            var selectedItem: Subscription? = null
                            items.forEachIndexed { index, value ->
                                if (index == 0) {
                                    value.selected = true
                                    selectedItem = value
                                } else {
                                    value.selected = false
                                }
                                dao.update(value)
                            }
                            context.eventsHelper.of<MainDrawerCallback>()
                                .onSubscriptionChange(selectedItem)
                        }
                        updateAdapterData(items)
                    }
                }
                noButton()
            }
        }

        override fun onSubscriptionAddClick() {
            val intent = Intent(requireContext(), NewSubscriptionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            startActivityForResult(intent, REQUEST_CODE_NEW_SUBSCRIPTION)
        }

        override fun onSubscriptionManageClick() {
            val intent = Intent(requireContext(), ManageSubscriptionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            startActivityForResult(intent, REQUEST_CODE_MANAGE_SUBSCRIPTION)
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