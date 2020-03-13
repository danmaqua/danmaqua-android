package moe.feng.danmaqua.util

import androidx.annotation.VisibleForTesting
import com.google.code.regexp.Pattern
import kotlinx.coroutines.runBlocking
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.data.DanmaquaDB
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.model.BlockedTextRule
import moe.feng.danmaqua.model.Subtitle

interface DanmakuFilter {

    companion object {

        fun acceptAll(): DanmakuFilter {
            return AcceptAllFilter
        }

        fun fromSettings(patternOnly: Boolean = false,
                         forceEnabledFiltered: Boolean = false): DanmakuFilter {
            return runBlocking {
                val db = DanmaquaDB.instance
                val pattern = if (forceEnabledFiltered || Settings.filterEnabled) {
                    Pattern.compile(db.patternRules().getSelected().pattern)
                } else {
                    null
                }
                DanmaquaFilter(
                    pattern = pattern,
                    blockedUids = if (patternOnly) emptyList() else
                        db.blockedUsers().getAll().map { it.uid },
                    blockedWords = if (patternOnly) emptyList() else Settings.blockedTextPatterns
                )
            }
        }

        @Throws(Exception::class)
        fun forTest(patternText: String?): DanmakuFilter? {
            val pattern = Pattern.compile(patternText)
            return DanmaquaFilter(pattern, emptyList(), emptyList())
        }

        @VisibleForTesting
        internal fun create(pattern: Pattern?,
                            blockedUids: List<Long>,
                            blockedWords: List<BlockedTextRule>): DanmakuFilter {
            return DanmaquaFilter(pattern, blockedUids, blockedWords)
        }

    }

    /**
     * Check if this danmaku should be showed
     *
     * @param msg Danmaku to check
     * @return Whether it or not to show this danmaku
     */
    operator fun invoke(msg: BiliChatDanmaku): Boolean

    fun unescapeSubtitle(msg: BiliChatDanmaku): Subtitle?

    private object AcceptAllFilter : DanmakuFilter {

        override fun invoke(msg: BiliChatDanmaku): Boolean {
            return true
        }

        override fun unescapeSubtitle(msg: BiliChatDanmaku): Subtitle? {
            return null to msg.text
        }

    }

    private class DanmaquaFilter(
        val pattern: Pattern?,
        val blockedUids: List<Long> = emptyList(),
        val blockedWords: List<BlockedTextRule> = emptyList()
    ) : DanmakuFilter {

        val hasTextGroup = pattern?.groupNames()?.contains("text") == true

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

        override fun unescapeSubtitle(msg: BiliChatDanmaku): Subtitle? {
            if (pattern == null) {
                return null to msg.text
            }
            val matcher = pattern.matcher(msg.text)
            if (matcher.find()) {
                if (hasTextGroup) {
                    val who = matcher.group("who")
                    val text = matcher.group("text")
                    if (text != null) {
                        return who to text
                    }
                }
                if (matcher.groupCount() > 0) {
                    return null to matcher.group(1)
                }
            }
            return null
        }

    }

}