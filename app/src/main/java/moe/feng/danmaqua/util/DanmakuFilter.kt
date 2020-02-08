package moe.feng.danmaqua.util

import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.model.BiliChatDanmaku
import java.util.regex.Pattern

interface DanmakuFilter {

    companion object {

        fun acceptAll(): DanmakuFilter {
            return AcceptAllFilter
        }

        fun fromSettings(): DanmakuFilter {
            if (!Settings.Filter.enabled) {
                return acceptAll()
            }
            val pattern = Pattern.compile(Settings.Filter.pattern)
            return DanmaquaFilter(pattern)
        }

    }

    /**
     * Check if this danmaku should be showed
     *
     * @param msg Danmaku to check
     * @return Whether it or not to show this danmaku
     */
    operator fun invoke(msg: BiliChatDanmaku): Boolean

    fun unescapeCaption(msg: BiliChatDanmaku): String?

    private object AcceptAllFilter : DanmakuFilter {

        override fun invoke(msg: BiliChatDanmaku): Boolean {
            return true
        }

        override fun unescapeCaption(msg: BiliChatDanmaku): String? {
            return msg.text
        }

    }

    private class DanmaquaFilter(
        val pattern: Pattern,
        val blockedUids: List<Long> = emptyList(),
        val blockedWords: List<Pattern> = emptyList()
    ) : DanmakuFilter {

        override fun invoke(msg: BiliChatDanmaku): Boolean {
            val matcher = pattern.matcher(msg.text)
            return matcher.matches()
        }

        override fun unescapeCaption(msg: BiliChatDanmaku): String? {
            val matcher = pattern.matcher(msg.text)
            if (matcher.find() && matcher.groupCount() > 0) {
                return matcher.group(1)
            }
            return null
        }

    }

}