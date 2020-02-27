package moe.feng.danmaqua.ui.settings.list

import android.view.ContextMenu
import android.view.MenuInflater
import android.view.View
import androidx.core.view.isGone
import kotlinx.android.synthetic.main.manage_pattern_rules_item.*
import moe.feng.danmaqua.R
import moe.feng.danmaqua.data.PatternRulesDao
import moe.feng.danmaqua.model.PatternRulesItem
import moe.feng.danmaqua.ui.list.ItemBasedSimpleViewBinder
import moe.feng.danmaqua.ui.list.ItemBasedViewHolder
import moe.feng.danmaqua.ui.list.viewHolderCreatorOf

class PatternRulesItemDelegate(var callback: Callback? = null)
    : ItemBasedSimpleViewBinder<PatternRulesItem, PatternRulesItemDelegate.ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = viewHolderCreatorOf(R.layout.manage_pattern_rules_item)

    interface Callback {

        fun onItemCheck(item: PatternRulesItem)

    }

    var contextData: PatternRulesItem? = null

    inner class ViewHolder(itemView: View) : ItemBasedViewHolder<PatternRulesItem>(itemView),
        View.OnCreateContextMenuListener {

        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        override fun onItemClick() {
            if (!data.selected) {
                data.selected = true
                callback?.onItemCheck(data)
            }
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

        override fun onBind() {
            radioButton.isChecked = data.selected
            titleText.text = data.title()
            descText.isGone = data.local
            descText.text = data.desc()
            if (data.local) {
                onlineInfoText.setText(R.string.local_rule_text)
            } else {
                onlineInfoText.text = context.getString(
                    R.string.commited_by_text_format, data.committer)
            }
        }

    }

}