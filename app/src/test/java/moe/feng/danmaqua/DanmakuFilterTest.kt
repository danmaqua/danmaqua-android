package moe.feng.danmaqua

import com.google.code.regexp.Pattern
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.BiliChatMessage
import moe.feng.danmaqua.util.DanmakuFilter
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DanmakuFilterTest {

    private val danmakuToTest = listOf(
        "路人甲【为什么】",
        "路人乙【嗯？】",
        "【没有发言人名的弹幕】",
        "测试【测试文本",
        "【Hello"
    )

    @Test
    fun test_unescapeSubtitle() {
        println(Danmaqua.DEFAULT_FILTER_PATTERN)
        val filter = DanmakuFilter.create(
            Pattern.compile(Danmaqua.DEFAULT_FILTER_PATTERN),
            emptyList(),
            emptyList()
        )
        danmakuToTest.forEach {
            println(filter.unescapeSubtitle(danmaku(it)))
        }
    }

    private fun danmaku(text: String): BiliChatDanmaku {
        return BiliChatDanmaku(
            cmd = BiliChatMessage.CMD_DANMAKU,
            text = text,
            senderName = "Tester",
            senderUid = 1L,
            timestamp = System.currentTimeMillis() / 1000L
        )
    }

}