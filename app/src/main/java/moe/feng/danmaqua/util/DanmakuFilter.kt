package moe.feng.danmaqua.util

import kotlinx.coroutines.runBlocking
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.data.DanmaquaDB
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.BlockedTextRule
import java.lang.Exception
import java.util.regex.Pattern

interface DanmakuFilter {

    companion object {

        fun acceptAll(): DanmakuFilter {
            return AcceptAllFilter
        }

        fun fromSettings(): DanmakuFilter {
            val pattern = if (Settings.Filter.enabled) {
                Pattern.compile(Settings.Filter.pattern)
            } else {
                null
            }
            return DanmaquaFilter(
                pattern = pattern,
                blockedUids = runBlocking {
                    DanmaquaDB.instance.blockedUsers().getAll().map { it.uid }
                },
                blockedWords = Settings.Filter.blockedTextPatterns
            )
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
        val pattern: Pattern?,
        val blockedUids: List<Long> = emptyList(),
        val blockedWords: List<BlockedTextRule> = emptyList()
    ) : DanmakuFilter {

        val blockedWordsPattern: List<Any> = blockedWords.mapNotNull {
            try {
                if (it.isRegExp) {
                    Pattern.compile(it.text)
                } else {
                    it.text
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        override fun invoke(msg: BiliChatDanmaku): Boolean {
            if (msg.senderUid in blockedUids) {
                return false
            }
            if (pattern != null) {
                val matcher = pattern.matcher(msg.text)
                if (!matcher.matches()) {
                    return false
                }
            }
            for (blockedPattern in blockedWordsPattern) {
                when (blockedPattern) {
                    is Pattern -> {
                        val blockedMatcher = blockedPattern.matcher(msg.text)
                        if (blockedMatcher.matches()) {
                            return false
                        }
                    }
                    is String -> {
                        if (blockedPattern in msg.text) {
                            return false
                        }
                    }
                }
            }
            return true
        }

        override fun unescapeCaption(msg: BiliChatDanmaku): String? {
            if (pattern == null) {
                return msg.text
            }
            val matcher = pattern.matcher(msg.text)
            if (matcher.find() && matcher.groupCount() > 0) {
                return matcher.group(1)
            }
            return null
        }

    }

}