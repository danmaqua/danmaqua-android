package moe.feng.danmaqua.ui.settings

import android.app.Activity
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.TooltipCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.manage_blocked_text_layout.*
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.R
import moe.feng.danmaqua.model.BlockedTextRule
import moe.feng.danmaqua.ui.BaseFragment

class ManageBlockedTextFragment : BaseFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.MANAGE_BLOCKED_TEXT"

    }

    private var items: List<BlockedTextRule> = emptyList()

    private val adapter: BlockedTextListAdapter = BlockedTextListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        items = Danmaqua.Settings.Filter.blockedTextPatterns
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.manage_blocked_text_layout, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            context.setTitle(R.string.manage_blocked_text_title)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter

        addButton.setOnClickListener(this::onAddButtonClick)
    }

    private fun onAddButtonClick(view: View) {
        showEditDialog(R.string.action_add_rule) { newValue ->
            items = (items.toMutableList() + newValue).toList()
            // TODO Use DiffUtil to notify
            adapter.notifyDataSetChanged()
            Danmaqua.Settings.Filter.blockedTextPatterns = items
            context?.let { Danmaqua.Settings.notifyChanged(it) }
        }
    }

    private fun onEditButtonClick(position: Int) {
        showEditDialog(R.string.action_edit_rule, items[position]) { newValue ->
            val mutableItems = items.toMutableList()
            mutableItems[position] = newValue
            items = mutableItems.toList()
            adapter.notifyDataSetChanged()
            Danmaqua.Settings.Filter.blockedTextPatterns = items
            context?.let { Danmaqua.Settings.notifyChanged(it) }
        }
    }

    private fun onDeleteButtonClick(position: Int) {
        val mutableItems = items.toMutableList()
        mutableItems.removeAt(position)
        items = mutableItems.toList()
        adapter.notifyDataSetChanged()
        Danmaqua.Settings.Filter.blockedTextPatterns = items
        context?.let { Danmaqua.Settings.notifyChanged(it) }
    }

    private fun showEditDialog(@StringRes titleRes: Int,
                               initialValue: BlockedTextRule = BlockedTextRule(""),
                               onOk: (newValue: BlockedTextRule) -> Unit) {
        val dialogView = LayoutInflater.from(context)
            .inflate(R.layout.manage_blocked_text_edit_dialog_layout, null)
        val editText = dialogView.findViewById<EditText>(android.R.id.edit)
        val checkboxRegexp = dialogView.findViewById<CheckBox>(R.id.checkboxRegexp)

        editText.setText(initialValue.text)
        checkboxRegexp.isChecked = initialValue.isRegExp

        AlertDialog.Builder(activity!!)
            .setTitle(titleRes)
            .setView(dialogView)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                val text = editText.text?.toString()
                if (text.isNullOrEmpty()) {
                    Toast.makeText(context!!, R.string.toast_empty_input, Toast.LENGTH_SHORT)
                        .show()
                    return@setPositiveButton
                }
                onOk(BlockedTextRule(text, checkboxRegexp.isChecked))
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
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

}