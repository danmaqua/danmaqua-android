package moe.feng.danmaqua.ui.main.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.*
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import moe.feng.danmaqua.Danmaqua.EXTRA_DATA
import moe.feng.danmaqua.R
import moe.feng.danmaqua.api.bili.RoomApi
import moe.feng.danmaqua.databinding.RoomInfoDialogContentBinding
import moe.feng.danmaqua.model.RoomInfo
import moe.feng.danmaqua.ui.common.dialog.BaseDialogFragment

class RoomInfoDialogFragment : BaseDialogFragment() {

    companion object {

        fun newInstance(roomId: Long): RoomInfoDialogFragment {
            return RoomInfoDialogFragment().apply {
                arguments = bundleOf(EXTRA_DATA to roomId)
            }
        }

    }

    private var roomInfo: RoomInfo? = null
    private var roomId: Long = 0L

    private lateinit var binding: RoomInfoDialogContentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            roomId = it.getLong(EXTRA_DATA)
        } ?: dismiss()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return buildAlertDialog {
            titleRes = R.string.room_info_title
            inflateDataBindingView<RoomInfoDialogContentBinding>(
                R.layout.room_info_dialog_content
            ) {
                binding = it
                onDialogViewCreated(savedInstanceState)
            }
            okButton()
            neutralButton(R.string.room_info_copy_url) {
                // TODO Copy url
            }
        }
    }

    private fun onDialogViewCreated(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            binding.room = null
            lifecycleScope.launch {
                roomInfo = RoomApi.getRoomInfo(roomId)
                binding.room = roomInfo
            }
        } else {
            roomInfo = savedInstanceState.getParcelable(EXTRA_DATA)
            binding.room = roomInfo
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        roomInfo?.let { outState.putParcelable(EXTRA_DATA, it) }
    }

}