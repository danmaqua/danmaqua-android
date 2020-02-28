package moe.feng.danmaqua.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.manage_blocked_text_layout.*
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BlockedTextRule
import moe.feng.danmaqua.ui.common.BaseFragment
import moe.feng.danmaqua.util.ext.*

class ManageBlockedTextFragment : BaseFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.MANAGE_BLOCKED_TEXT"

    }

    private var items: List<BlockedTextRule> = emptyList()

    private val adapter: BlockedTextListAdapter = BlockedTextListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        items = Settings.blockedTextPatterns
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.manage_blocked_text_layout, container, false)
    }

    override fun getActivityTitle(context: Context): String? {
        return context.getString(R.string.manage_blocked_text_title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter

        addButton.setOnClickListener(this::onAddButtonClick)
    }

    private fun setItems(items: List<BlockedTextRule>) {
        val oldItems = this.items
        val result = DiffUtil.calculateDiff(ListDiffCallback(oldItems, items))
        this.items = items
        result.dispatchUpdatesTo(this.adapter)
    }

    private fun onAddButtonClick(view: View) {
        showEditDialog(R.string.action_add_rule) { newValue ->
            setItems((items.toMutableList() + newValue).toList())
            Settings.commit {
                blockedTextPatterns = items
            }
        }
    }

    private fun onEditButtonClick(position: Int) {
        showEditDialog(R.string.action_edit_rule, items[position]) { newValue ->
            val mutableItems = items.toMutableList()
            mutableItems[position] = newValue
            setItems(mutableItems.toList())
            Settings.commit {
                blockedTextPatterns = items
            }
        }
    }

    private fun onDeleteButtonClick(position: Int) {
        val mutableItems = items.toMutableList()
        mutableItems.removeAt(position)
        setItems(mutableItems.toList())
        Settings.commit {
            blockedTextPatterns = items
        }
    }

    private fun showEditDialog(@StringRes titleResource: Int,
                               initialValue: BlockedTextRule = BlockedTextRule(""),
                               onOk: (newValue: BlockedTextRule) -> Unit) {
        showAlertDialog {
            lateinit var editText: EditText
            lateinit var checkboxRegexp: CheckBox

            titleRes = titleResource
            inflateView(R.layout.manage_blocked_text_edit_dialog_layout) {
                editText = it.findViewById(android.R.id.edit)
                checkboxRegexp = it.findViewById(R.id.checkboxRegexp)

                editText.setText(initialValue.text)
                checkboxRegexp.isChecked = initialValue.isRegExp
            }
            okButton {
                val text = editText.text?.toString()
                if (text.isNullOrEmpty()) {
                    Toast.makeText(context, R.string.toast_empty_input, Toast.LENGTH_SHORT)
                        .show()
                    return@okButton
                }
                onOk(BlockedTextRule(text, checkboxRegexp.isChecked))
            }
            cancelButton()
        }
    }

    private inner class BlockedTextListAdapter
        : RecyclerView.Adapter<BlockedTextListAdapter.ViewHolder>() {

        private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val textView: TextView = itemView.findViewById(android.R.id.text1)
            val editButton: View = itemView.findViewById(R.id.editButton)
            val delButton: View = itemView.findViewById(R.id.deleteButton)

            init {
                editButton.setOnClickListener { onEditButtonClick(adapterPosition) }
                delButton.setOnClickListener { onDeleteButtonClick(adapterPosition) }
                TooltipCompat.setTooltipText(editButton,
                    itemView.context.getString(R.string.action_edit_rule))
                TooltipCompat.setTooltipText(delButton,
                    itemView.context.getString(R.string.action_delete_rule))
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.manage_blocked_text_item, parent, false))
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.text = items[position].text
        }

    }

    private class ListDiffCallback(val oldItems: List<BlockedTextRule>,
                                   val newItems: List<BlockedTextRule>) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldItems[oldItemPosition]
            val newItem = newItems[newItemPosition]
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return areItemsTheSame(oldItemPosition, newItemPosition)
        }

    }

}