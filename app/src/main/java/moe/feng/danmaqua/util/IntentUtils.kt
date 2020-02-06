package moe.feng.danmaqua.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import moe.feng.danmaqua.R

object IntentUtils {

    fun view(uri: String): Intent {
        return view(uri.toUri())
    }

    fun view(uri: Uri): Intent {
        return Intent(Intent.ACTION_VIEW, uri)
    }

    fun openBilibiliLive(context: Context, roomId: Long): Intent {
        val biliSchemaIntent = view("bilibili://live/$roomId")
        if (biliSchemaIntent.resolveActivity(context.packageManager) != null) {
            return biliSchemaIntent
        }
        val linkIntent = view("https://live.bilibili.com/$roomId")
        return Intent.createChooser(linkIntent,
            context.getString(R.string.chooser_title_choose_apps_to_watch_stream))
    }

}