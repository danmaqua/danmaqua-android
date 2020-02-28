package moe.feng.danmaqua.ui.proxy

import android.os.Bundle
import moe.feng.danmaqua.ui.common.BaseActivity
import moe.feng.danmaqua.util.IntentUtils

class B23ProxyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri = intent?.data
        if (uri != null) {
            if (uri.host == "b23.tv") {
                startActivity(IntentUtils.openBilibiliVideo(this, uri.path!!))
            }
        }

        finish()
    }

}