package moe.feng.danmaqua.data

import androidx.room.*
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.model.PatternRulesItem
import moe.feng.danmaqua.model.buildTextTranslation
import moe.feng.danmaqua.model.chinese
import moe.feng.danmaqua.model.english

@Dao
abstract class PatternRulesDao {

    companion object {

        const val DEFAULT_ID = "7ebced82-147a-47e8-8981-4357c77dde4f"

        private val DEFAULT_ITEM = PatternRulesItem(
            id = DEFAULT_ID,
            title = buildTextTranslation {
                english = "Recommended pattern"
                chinese = "推荐规则"
            },
            desc = buildTextTranslation {
                chinese = "能够适应大多数同传大佬的习惯，支持多种括号，" +
                        "且接受人名在括号左边的格式。"
            },
            committer = "fython",
            pattern = Danmaqua.DEFAULT_FILTER_PATTERN,
            local = false
        )

    }

    suspend fun addDefaultItem() {
        if (findById(DEFAULT_ID) == null) {
            DEFAULT_ITEM.selected = findSelected() == null
            add(DEFAULT_ITEM)
        } else {
            update(DEFAULT_ITEM)
        }
    }

    @Insert
    abstract suspend fun add(vararg rules: PatternRulesItem)

    @Update
    abstract suspend fun update(vararg rules: PatternRulesItem)

    @Delete
    abstract suspend fun delete(rules: PatternRulesItem)

    @Query("SELECT * FROM pattern_rules")
    abstract suspend fun getAll(): List<PatternRulesItem>

    @Query("SELECT * FROM pattern_rules WHERE local = 1")
    abstract suspend fun getLocalOnly(): List<PatternRulesItem>

    @Query("SELECT * FROM pattern_rules WHERE local = 0")
    abstract suspend fun getFromOnline(): List<PatternRulesItem>

    @Query("SELECT * FROM pattern_rules WHERE id = :id")
    abstract suspend fun findById(id: String): PatternRulesItem?

    @Query("SELECT * FROM pattern_rules WHERE selected = 1 LIMIT 1")
    abstract suspend fun findSelected(): PatternRulesItem?

    suspend fun getSelected(): PatternRulesItem {
        val selected = findSelected()
        if (selected == null) {
            addDefaultItem()
        }
        return selected ?: DEFAULT_ITEM
    }

}