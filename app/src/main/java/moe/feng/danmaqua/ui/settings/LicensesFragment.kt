package moe.feng.danmaqua.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moe.feng.danmaqua.Danmaqua.ACTION_PREFIX
import moe.feng.danmaqua.R
import moe.feng.danmaqua.ui.BaseFragment

class LicensesFragment : BaseFragment() {

    companion object {

        const val ACTION = "$ACTION_PREFIX.settings.LICENSES"

    }

    override fun getActivityTitle(context: Context): String? {
        return context.getString(R.string.about_open_source_licenses_title)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

}