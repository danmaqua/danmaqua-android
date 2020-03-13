package moe.feng.danmaqua.ui.subscription

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.*
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.view.avatarUrl
import com.drakeet.multitype.ItemViewDelegate
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.manage_subscription_activity.*
import kotlinx.android.synthetic.main.manage_subscription_item_view.*
import kotlinx.coroutines.launch
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.ui.common.BaseActivity
import moe.feng.danmaqua.ui.common.list.BaseViewHolder
import moe.feng.danmaqua.util.ShortcutsUtils
import java.util.*

class ManageSubscriptionActivity : BaseActivity() {

    private val list: MutableList<Subscription> = mutableListOf()
    private val adapter: MultiTypeAdapter = MultiTypeAdapter(list).also {
        it.register(ManageSubscriptionItemViewDelegate())
    }
    private val itemTouchHelperCallback: ManageItemTouchHelperCallback
            = ManageItemTouchHelperCallback()
    private val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_subscription_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        itemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView.adapter = adapter
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                emptyView.isVisible = adapter.itemCount == 0
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                onChanged()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                onChanged()
            }
        })

        lifecycleScope.launch {
            list.clear()
            list.addAll(database.subscriptions().getAll())
            adapter.notifyDataSetChanged()
        }
    }

    private inner class ManageItemTouchHelperCallback : ItemTouchHelper.Callback() {

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if (viewHolder is ManageSubscriptionItemViewDelegate.ViewHolder) {
                return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0)
            }
            return 0
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            viewHolder.itemView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
            Collections.swap(list, viewHolder.adapterPosition, target.adapterPosition)
            for ((index, item) in list.withIndex()) {
                item.order = index
                lifecycleScope.launch { database.subscriptions().update(item) }
            }
            setResult(RESULT_OK)
            adapter.notifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            throw UnsupportedOperationException()
        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                if (viewHolder is ManageSubscriptionItemViewDelegate.ViewHolder) {
                    viewHolder.raiseUp()
                }
            }
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            if (viewHolder is ManageSubscriptionItemViewDelegate.ViewHolder) {
                viewHolder.liftDown()
            }
        }

    }

    inner class ManageSubscriptionItemViewDelegate :
        ItemViewDelegate<Subscription, ManageSubscriptionItemViewDelegate.ViewHolder>() {

        @SuppressLint("ClickableViewAccessibility")
        inner class ViewHolder(itemView: View) : BaseViewHolder(itemView) {

            val favIconColor: Int get() = context.getColor(R.color.favourite_icon_checked)
            val notFavIconColor: Int get() = context.getColor(R.color.favourite_icon_normal)
            val shortAnimTime: Long get() = context.resources
                .getInteger(android.R.integer.config_shortAnimTime).toLong()

            lateinit var data: Subscription

            var dragAnimator: Animator? = null

            var favAnimator: Animator? = null

            init {
                dragHandle.setOnTouchListener { _, event ->
                    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                        itemTouchHelper.startDrag(this)
                        return@setOnTouchListener true
                    }
                    false
                }

                favButton.setOnClickListener {
                    launch {
                        val dao = database.subscriptions()
                        if (!data.favourite) {
                            val favCount = dao.getFavourites().size
                            if (favCount == 3) {
                                Toast.makeText(context, R.string.toast_favourite_limit,
                                    Toast.LENGTH_SHORT).show()
                                return@launch
                            }
                        }
                        data.favourite = !data.favourite
                        dao.update(data)
                        favAnimator?.cancel()
                        favAnimator = ObjectAnimator.ofArgb(
                            if (data.favourite) notFavIconColor else favIconColor,
                            if (data.favourite) favIconColor else notFavIconColor
                        ).also { anim ->
                            anim.duration = shortAnimTime
                            anim.addUpdateListener {
                                favButton.imageTintList =
                                    ColorStateList.valueOf(it.animatedValue as Int)
                            }
                            anim.start()
                        }
                        favButton.setImageResource(if (data.favourite)
                            R.drawable.ic_favorite_24 else R.drawable.ic_favorite_border_24)
                        ShortcutsUtils.requestUpdateShortcuts(context)
                    }
                }

                deleteButton.setOnClickListener {
                    showAlertDialog {
                        titleRes = R.string.unsubscribe_dialog_title
                        message = getString(R.string.unsubscribe_dialog_message, data.username)
                        yesButton {
                            launch {
                                val dao = database.subscriptions()
                                val lastSelected = data.selected
                                dao.delete(data)
                                val items = dao.getAll()
                                if (lastSelected) {
                                    items.forEachIndexed { index, value ->
                                        value.selected = index == 0
                                        dao.update(value)
                                    }
                                }
                                setResult(RESULT_OK)
                                list.clear()
                                list.addAll(items)
                                adapter.notifyDataSetChanged()
                            }
                        }
                        noButton()
                    }
                }
            }

            fun raiseUp() {
                dragAnimator?.cancel()
                dragAnimator = ObjectAnimator.ofFloat(
                    itemView, "elevation",
                    0F, itemView.resources.getDimension(R.dimen.raised_list_item_elevation)
                )
                dragAnimator?.duration = shortAnimTime / 2
                dragAnimator?.start()
            }

            fun liftDown() {
                dragAnimator?.cancel()
                dragAnimator = ObjectAnimator.ofFloat(
                    itemView, "elevation",
                    itemView.resources.getDimension(R.dimen.raised_list_item_elevation), 0F
                )
                dragAnimator?.duration = shortAnimTime / 2
                dragAnimator?.start()
            }

        }

        override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
            return ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.manage_subscription_item_view, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, item: Subscription) {
            with(holder) {
                data = item
                avatarView.avatarUrl = item.avatar
                nameText.text = item.username
                if (item.favourite) {
                    favButton.imageTintList = ColorStateList.valueOf(favIconColor)
                    favButton.setImageResource(R.drawable.ic_favorite_24)
                } else {
                    favButton.imageTintList = ColorStateList.valueOf(notFavIconColor)
                    favButton.setImageResource(R.drawable.ic_favorite_border_24)
                }
            }
        }

        override fun onViewRecycled(holder: ViewHolder) {
            holder.favAnimator?.cancel()
        }

    }

}