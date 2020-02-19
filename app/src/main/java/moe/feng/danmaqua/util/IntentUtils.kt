package moe.feng.danmaqua.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import moe.feng.danmaqua.R

object IntentUtils {

    private const val ALIPAY_CODE_SIUBENG = "aex07585vva8hhbzjfvv1da"

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

    fun openBilibiliVideo(context: Context, path: String): Intent {
        val linkIntent = view("https://www.bilibili.com/video/$path")
        return Intent.createChooser(linkIntent,
            context.getString(R.string.chooser_title_choose_apps_to_watch_stream))
    }

    fun sendMail(address: String): Intent {
        return Intent(Intent.ACTION_SENDTO, "mailto:$address".toUri())
    }

    fun openAlipayTransfer(targetCode: String = ALIPAY_CODE_SIUBENG): Intent {
        val qrCodeUrl = "https://qr.alipay.com/$targetCode?_s=web-other"
        val uri = Uri.Builder()
            .scheme("alipayqr")
            .authority("platformapi")
            .appendPath("startapp")
            .appendQueryParameter("saId", "10000007")
            .appendQueryParameter("clientVersion", "3.7.0.0718")
            .appendQueryParameter("qrcode", qrCodeUrl)
            .appendQueryParameter("_t", System.currentTimeMillis().toString())
            .build()
        return Intent(Intent.ACTION_VIEW, uri)
    }

}