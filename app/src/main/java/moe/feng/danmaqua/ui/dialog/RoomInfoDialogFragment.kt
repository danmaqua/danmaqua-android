package moe.feng.danmaqua.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.view.isGone
import androidx.core.view.isVisible
import kotlinx.coroutines.launch
import moe.feng.danmaqua.Danmaqua.EXTRA_DATA
import moe.feng.danmaqua.R
import moe.feng.danmaqua.api.RoomApi
import moe.feng.danmaqua.model.RoomInfo

class RoomInfoDialogFragment : BaseDialogFragment() {

    companion object {

        fun newInstance(roomId: Long): RoomInfoDialogFragment {
            return RoomInfoDialogFragment().apply {
                arguments = Bundle().also {
                    it.putLong(EXTRA_DATA, roomId)
                }
            }
        }

    }

    private lateinit var loadingView: View
    private lateinit var contentView: View
    private lateinit var roomTitle: TextView
    private lateinit var userName: TextView
    private lateinit var roomIdView: TextView
    private lateinit var roomArea: TextView
    private lateinit var liveStatus: TextView

    private var roomInfo: RoomInfo? = null
    private var roomId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            roomId = it.getLong(EXTRA_DATA)
        } ?: dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val context = ContextThemeWrapper(
            activity!!, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
        val view = LayoutInflater.from(context).inflate(R.layout.room_info_dialog_content, null)
        onDialogViewCreated(view, savedInstanceState)
        return AlertDialog.Builder(context)
            .setTitle(R.string.room_info_title)
            .setView(view)
            .setPositiveButton(android.R.string.ok, null)
            .setNeutralButton(R.string.room_info_copy_url) { _, _ ->
                // TODO Copy url
            }
            .create()
    }

    private fun onDialogViewCreated(view: View, savedInstanceState: Bundle?) {
        loadingView = view.findViewById(R.id.loadingView)
        contentView = view.findViewById(R.id.contentView)
        roomTitle = view.findViewById(R.id.roomTitle)
        userName = view.findViewById(R.id.userName)
        roomIdView = view.findViewById(R.id.roomId)
        roomArea = view.findViewById(R.id.roomArea)
        liveStatus = view.findViewById(R.id.liveStatus)

        if (savedInstanceState == null) {
            loadingView.isVisible = true
            contentView.isGone = true
            launch {
                roomInfo = RoomApi.getRoomInfo(roomId)
                bindViews()
            }
        } else {
            roomInfo = savedInstanceState.getParcelable(EXTRA_DATA)
            if (roomInfo != null) {
                bindViews()
            } else {
                loadingView.isVisible = true
                contentView.isGone = true
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        roomInfo?.let { outState.putParcelable(EXTRA_DATA, it) }
    }

    private fun bindViews() {
        roomInfo?.let {
            loadingView.isGone = true
            contentView.isVisible = true
            roomTitle.text = it.data.title
            userName.text = it.data.uid.toString()
            roomIdView.text = it.data.roomId.toString()
            roomArea.text = it.data.parentAreaName + "-" + it.data.areaName
            liveStatus.setText(if (it.data.liveStatus == 1) {
                R.string.live_status_active
            } else {
                R.string.live_status_closed
            })
        }
    }

}