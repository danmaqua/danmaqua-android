package moe.feng.danmaqua.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.manage_blocked_users_layout.*
import kotlinx.coroutines.launch
import moe.feng.danmaqua.Danmaqua
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.R
import moe.feng.danmaqua.api.bili.UserApi
import moe.feng.danmaqua.model.BlockedUserRule
import moe.feng.danmaqua.model.SpaceInfo
import moe.feng.danmaqua.ui.BaseFragment
import moe.feng.danmaqua.ui.view.CircleImageView

class ManageBlockedUsersFragment : BaseFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.MANAGE_BLOCKED_USERS"

    }

    private var items: List<BlockedUserRule> = emptyList()

    private val adapter: BlockedUsersListAdapter = BlockedUsersListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.manage_blocked_users_layout, container, false)
    }

    override fun getActivityTitle(context: Context): String? {
        return context.getString(R.string.manage_blocked_user_title)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.adapter = adapter

        addButton.setOnClickListener(this::onAddButtonClick)

        launch {
            items = database.blockedUsers().getAll()
            adapter.notifyDataSetChanged()
        }
    }

    private fun onAddButtonClick(view: View) {
        val dialogView = LayoutInflater.from(activity!!)
            .inflate(R.layout.manage_blocked_user_edit_dialog_layout, null)
        val editText = dialogView.findViewById<TextInputEditText>(android.R.id.edit)
        AlertDialog.Builder(activity!!)
            .setTitle(R.string.add_blocked_user_title)
            .setView(dialogView)
            .setPositiveButton(R.string.action_search) { _, _ ->
                val context = editText.context
                val uid = editText.text?.toString()?.toLongOrNull() ?: 0L
                if (uid <= 0) {
                    Toast.makeText(
                        context,
                        R.string.toast_empty_input,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }
                launch {
                    val info = UserApi.getSpaceInfo(uid)
                    if (info.code == 0) {
                        showConfirmDialog(info)
                    } else {
                        Toast.makeText(
                            context,
                            context.getString(R.string.toast_cannot_find_user_by_uid_format, uid),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun onDeleteButtonClick(position: Int) {
        launch {
            database.blockedUsers().delete(items[position])
            setItems(database.blockedUsers().getAll())
            context?.let { Danmaqua.Settings.notifyChanged(it) }
        }
    }

    private fun showConfirmDialog(info: SpaceInfo) {
        val dialogView = LayoutInflater.from(activity!!)
            .inflate(R.layout.manage_blocked_user_confirm_add_dialog_layout, null)
        val avatarView = dialogView.findViewById<CircleImageView>(R.id.avatarView)
        val usernameView = dialogView.findViewById<TextView>(R.id.usernameView)
        val uidView = dialogView.findViewById<TextView>(R.id.uidView)
        Picasso.get()
            .load(info.data.face)
            .placeholder(R.drawable.avatar_placeholder_empty)
            .into(avatarView)
        usernameView.text = info.data.name
        uidView.text = getString(R.string.uid_text_format, info.data.uid)
        AlertDialog.Builder(activity!!)
            .setTitle(R.string.confirm_add_blocked_user_title)
            .setView(dialogView)
            .setPositiveButton(android.R.string.yes) { _, _ ->
                launch {
                    database.blockedUsers().add(BlockedUserRule(
                        info.data.uid,
                        info.data.name,
                        info.data.face
                    ))
                    setItems(database.blockedUsers().getAll())
                    context?.let { Danmaqua.Settings.notifyChanged(it) }
                }
            }
            .setNegativeButton(android.R.string.no, null)
            .show()
    }

    private fun setItems(items: List<BlockedUserRule>) {
        val oldItems = this.items
        val result = DiffUtil.calculateDiff(ListDiffCallback(oldItems, items))
        this.items = items
        result.dispatchUpdatesTo(this.adapter)
    }

    private inner class BlockedUsersListAdapter
        : RecyclerView.Adapter<BlockedUsersListAdapter.ViewHolder>() {

        private inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val avatarView: CircleImageView = itemView.findViewById(R.id.avatarView)
            val usernameView: TextView = itemView.findViewById(R.id.usernameView)
            val uidView: TextView = itemView.findViewById(R.id.uidView)
            val deleteButton: View = itemView.findViewById(R.id.deleteButton)

            init {
                deleteButton.setOnClickListener { onDeleteButtonClick(adapterPosition) }
            }

        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.manage_blocked_user_item, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            with(holder) {
                usernameView.text = item.username
                uidView.text = getString(R.string.uid_text_format, item.uid)
                Picasso.get()
                    .load(item.face)
                    .placeholder(R.drawable.avatar_placeholder_empty)
                    .into(avatarView)
            }
        }

    }

    private class ListDiffCallback(val oldItems: List<BlockedUserRule>,
                                   val newItems: List<BlockedUserRule>): DiffUtil.Callback() {

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