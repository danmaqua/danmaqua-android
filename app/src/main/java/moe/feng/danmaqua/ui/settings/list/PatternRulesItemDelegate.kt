package moe.feng.danmaqua.ui.settings.list

import android.view.ContextMenu
import android.view.MenuInflater
import android.view.View
import moe.feng.danmaqua.R
import moe.feng.danmaqua.data.PatternRulesDao
import moe.feng.danmaqua.databinding.ManagePatternRulesItemBinding
import moe.feng.danmaqua.model.PatternRulesItem
import moe.feng.danmaqua.ui.list.*
import moe.feng.danmaqua.ui.settings.list.PatternRulesItemDelegate.ViewHolder

class PatternRulesItemDelegate(var callback: Callback? = null)
    : ItemBasedSimpleViewBinder<PatternRulesItem, ViewHolder>() {

    override val viewHolderCreator: ViewHolderCreator<ViewHolder>
        = innerDataBindingViewHolderCreatorOf(R.layout.manage_pattern_rules_item)

    interface Callback {

        fun onItemCheck(item: PatternRulesItem)

    }

    var contextData: PatternRulesItem? = null

    inner class ViewHolder(dataBinding: ManagePatternRulesItemBinding)
        : DataBindingViewHolder<PatternRulesItem, ManagePatternRulesItemBinding>(dataBinding),
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
            with (menu) {
                findItem(R.id.action_edit).isVisible = data.local
                findItem(R.id.action_delete).isVisible =
                    data.local && data.id != PatternRulesDao.DEFAULT_ID
                findItem(R.id.action_info).isVisible = !data.local
                setHeaderTitle(data.title())
            }
        }

    }

}