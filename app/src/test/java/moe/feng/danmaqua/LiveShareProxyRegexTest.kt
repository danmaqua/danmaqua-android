package moe.feng.danmaqua

import com.google.code.regexp.Pattern
import moe.feng.danmaqua.ui.proxy.LiveShareProxyActivity
import org.junit.Assert.assertTrue
import org.junit.Test

class LiveShareProxyRegexTest {

    @Test
    fun test_Regex() {
        val pattern = Pattern.compile(LiveShareProxyActivity.REGEX)
        val text = "湊-阿库娅Official正在bilibili直播，快来了解一下吧～" +
                "https://live.bilibili.com/14917277"
        val matcher = pattern.matcher(text)
        assertTrue(matcher.find())
        assertTrue(matcher.group(2) == "14917277")
    }

}