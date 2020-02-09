package moe.feng.danmaqua.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.new_subscription_activity.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.Danmaqua.EXTRA_DATA
import moe.feng.danmaqua.R
import moe.feng.danmaqua.api.RoomApi
import moe.feng.danmaqua.api.UserApi
import moe.feng.danmaqua.model.Subscription
import java.lang.Exception

class NewSubscriptionActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.new_subscription_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        subscribeButton.setOnClickListener {
            launch {
                val id = roomIdEdit.text.toString().trim().toLongOrNull() ?: 0L
                if (id <= 0) {
                    Toast.makeText(
                        this@NewSubscriptionActivity,
                        R.string.toast_invalid_room_id,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                try {
                    val subscription = getSubscription(id)
                    val result = Intent()
                    result.putExtra(EXTRA_DATA, subscription)
                    setResult(Activity.RESULT_OK, result)
                    finish()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private suspend fun getSubscription(roomId: Long): Subscription = withContext(Dispatchers.IO) {
        val roomInitInfo = RoomApi.getRoomInitInfo(roomId)
        val spaceInfo = UserApi.getSpaceInfo(roomInitInfo.data.uid)
        Subscription(spaceInfo.data.uid, roomId, spaceInfo.data.name, spaceInfo.data.face)
    }

}