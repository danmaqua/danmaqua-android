package moe.feng.danmaqua.ui.settings.pattern

import android.content.Context
import android.view.*
import androidx.core.view.isGone
import com.drakeet.multitype.ItemViewDelegate
import kotlinx.android.synthetic.main.manage_pattern_rules_item.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.data.PatternRulesDao
import moe.feng.danmaqua.model.PatternRulesItem
import moe.feng.danmaqua.ui.list.BaseViewHolder

class PatternRulesItemDelegate(var callback: Callback? = null)
    : ItemViewDelegate<PatternRulesItem, PatternRulesItemDelegate.ViewHolder>() {

    interface Callback {

        fun onItemCheck(item: PatternRulesItem)

    }

    var contextData: PatternRulesItem? = null

    inner class ViewHolder(itemView: View) : BaseViewHolder(itemView),
        View.OnCreateContextMenuListener {

        lateinit var data: PatternRulesItem

        init {
            itemView.setOnClickListener {
                if (!data.selected) {
                    data.selected = true
                    callback?.onItemCheck(data)
                }
            }
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onCreateContextMenu(
            menu: ContextMenu,
            v: View,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            contextData = data
            MenuInflater(itemView.context).inflate(R.menu.context_menu_pattern_rules, menu)
            menu.findItem(R.id.action_edit).isVisible = data.local
            menu.findItem(R.id.action_delete).isVisible =
                data.local && data.id != PatternRulesDao.DEFAULT_ID
            menu.findItem(R.id.action_info).isVisible = !data.local
            menu.setHeaderTitle(data.title())
        }

    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.manage_pattern_rules_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, item: PatternRulesItem) {
        with(holder) {
            data = item
            radioButton.isChecked = item.selected
            titleText.text = item.title()
            descText.isGone = item.local
            descText.text = item.desc()
            if (item.local) {
                onlineInfoText.setText(R.string.local_rule_text)
            } else {
                onlineInfoText.text = context.getString(
                    R.string.commited_by_text_format, item.committer)
            }
        }
    }

}