package moe.feng.danmaqua.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import kotlinx.android.synthetic.main.main_danmaku_context_menu_dialog_layout.*
import moe.feng.common.eventshelper.of
import moe.feng.danmaqua.Danmaqua.EXTRA_DATA
import moe.feng.danmaqua.R
import moe.feng.danmaqua.event.MainDanmakuContextMenuListener
import moe.feng.danmaqua.model.BiliChatDanmaku
import moe.feng.danmaqua.ui.dialog.BaseBottomSheetDialogFragment
import moe.feng.danmaqua.util.ext.eventsHelper

class DanmakuContextMenuDialogFragment : BaseBottomSheetDialogFragment() {

    companion object {

        fun newInstance(data: BiliChatDanmaku): DanmakuContextMenuDialogFragment {
            return DanmakuContextMenuDialogFragment().apply {
                arguments = bundleOf(EXTRA_DATA to data)
            }
        }

    }

    private lateinit var danmaku: BiliChatDanmaku

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        danmaku = arguments!!.getParcelable(EXTRA_DATA)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_danmaku_context_menu_dialog_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        usernameView.text = danmaku.senderName
        contentTextView.text = danmaku.text

        val listener = view.context.eventsHelper.of<MainDanmakuContextMenuListener>()

        blockTextButton.setOnClickListener {
            listener.onConfirmBlockText(danmaku)
            dismiss()
        }
        blockUserButton.setOnClickListener {
            listener.onConfirmBlockUser(danmaku)
            dismiss()
        }
        hideButton.setOnClickListener {
            listener.onHideDanmaku(danmaku)
            dismiss()
        }
    }

}