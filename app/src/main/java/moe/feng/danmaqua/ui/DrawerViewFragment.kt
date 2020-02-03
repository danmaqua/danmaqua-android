package moe.feng.danmaqua.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.android.synthetic.main.main_drawer_view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.DanmaquaApplication
import moe.feng.danmaqua.R
import moe.feng.danmaqua.api.RoomApi
import moe.feng.danmaqua.api.UserApi
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.ui.list.SubscriptionAddItemViewDelegate
import moe.feng.danmaqua.ui.list.SubscriptionItemViewDelegate

class DrawerViewFragment : BaseFragment() {

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

        val db = DanmaquaApplication.getDatabase(view.context)
        launch {
            //drawerListAdapter.items = db.subscriptions().getAll()
            drawerListAdapter.items = withContext(Dispatchers.IO) {
                listOf(
                    getSubscription(RoomApi.MINATO_AQUA_ROOM_ID).apply { selected = true },
                    getSubscription(1996).apply { selected = false },
                    SubscriptionAddItemViewDelegate.Item
                )
            }
            drawerListAdapter.notifyDataSetChanged()
        }
    }

    private suspend fun getSubscription(roomId: Long): Subscription = withContext(Dispatchers.IO) {
        val roomInitInfo = RoomApi.getRoomInfo(roomId)
        val spaceInfo = UserApi.getSpaceInfo(roomInitInfo.data.uid)
        Subscription(spaceInfo.data.uid, roomId, spaceInfo.data.name, spaceInfo.data.face)
    }

    private inner class DrawerListAdapter : MultiTypeAdapter(),
        SubscriptionItemViewDelegate.Callback, SubscriptionAddItemViewDelegate.Callback {

        init {
            register(SubscriptionItemViewDelegate(this))
            register(SubscriptionAddItemViewDelegate(this))
        }

        override fun onSubscriptionItemClick(item: Subscription) {
            // TODO Change data in database
            callback?.onSubscriptionChange(item)
        }

        override fun onSubscriptionAddClick() {

        }

    }

}