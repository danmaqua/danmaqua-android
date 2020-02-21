package moe.feng.danmaqua.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import androidx.core.graphics.scale
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moe.feng.danmaqua.Danmaqua.EXTRA_DATA
import moe.feng.danmaqua.R
import moe.feng.danmaqua.data.DanmaquaDB
import moe.feng.danmaqua.model.Subscription
import moe.feng.danmaqua.ui.proxy.QuickStartShortcutActivity

object ShortcutsUtils {

    fun requestUpdateShortcuts(context: Context) {
        MainScope().launch {
            updateShortcuts(context)
        }
    }

    suspend fun updateShortcuts(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val manager = context.getSystemService<ShortcutManager>() ?: return
            val favourites = DanmaquaDB.instance.subscriptions().getFavourites()
            manager.dynamicShortcuts = favourites.map { item ->
                subscriptionToShortcutInfo(context, item)
            }
        }
    }

    suspend fun requestPinSubscription(context: Context, item: Subscription): Boolean {
        val shortcut = subscriptionToShortcutInfoCompat(context, item)
        return ShortcutManagerCompat.requestPinShortcut(context, shortcut, null)
    }

    private suspend fun subscriptionToShortcutInfoCompat(
        context: Context, item: Subscription
    ): ShortcutInfoCompat {
        val intent = Intent(context, QuickStartShortcutActivity::class.java)
            .setAction(Intent.ACTION_VIEW)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(EXTRA_DATA, item.roomId)
        val icon = withContext(Dispatchers.IO) {
            Picasso.get().load(item.avatar).get()?.let { bitmap ->
                IconCompat.createWithBitmap(BitmapUtils.circular(bitmap))
            }
        }
        return ShortcutInfoCompat.Builder(context, "room_${item.roomId}")
            .setActivity(ComponentName(context, QuickStartShortcutActivity::class.java))
            .setIntent(intent)
            .setShortLabel(item.username)
            .setLongLabel(item.username)
            .setIcon(icon)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private suspend fun subscriptionToShortcutInfo(
        context: Context, item: Subscription
    ): ShortcutInfo {
        val manager = context.getSystemService<ShortcutManager>()!!
        val intent = Intent(context, QuickStartShortcutActivity::class.java)
            .setAction(Intent.ACTION_VIEW)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            .putExtra(EXTRA_DATA, item.roomId)
        val icon = withContext(Dispatchers.IO) {
            Picasso.get().load(item.avatar)
                .placeholder(R.drawable.avatar_placeholder_empty)
                .get()
                ?.let { bitmap ->
                    if (bitmap.height > manager.iconMaxHeight ||
                        bitmap.width > manager.iconMaxWidth) {
                        bitmap.scale(manager.iconMaxWidth, manager.iconMaxHeight)
                    } else {
                        bitmap
                    }
                }
                ?.let { bitmap ->
                    Icon.createWithBitmap(BitmapUtils.circular(bitmap))
                }
        }
        return ShortcutInfo.Builder(context, "room_${item.roomId}")
            .setIntent(intent)
            .setShortLabel(item.username)
            .setLongLabel(item.username)
            .setIcon(icon)
            .build()
    }

}