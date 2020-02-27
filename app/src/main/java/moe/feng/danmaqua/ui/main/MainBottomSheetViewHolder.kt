package moe.feng.danmaqua.ui.main

import android.view.View
import android.view.WindowInsets
import android.widget.FrameLayout
import androidx.core.view.GravityCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.filter_bottom_sheet_fragment.*
import kotlinx.android.synthetic.main.main_activity.*
import moe.feng.danmaqua.Danmaqua.Settings
import moe.feng.danmaqua.event.SettingsChangedListener
import moe.feng.danmaqua.ui.MainActivity
import moe.feng.danmaqua.ui.settings.FilterSettingsFragment
import moe.feng.danmaqua.ui.settings.PreferenceActivity
import moe.feng.danmaqua.ui.settings.pattern.ManagePatternRulesFragment
import moe.feng.danmaqua.util.ext.eventsHelper

class MainBottomSheetViewHolder(private val mainActivity: MainActivity)
    : LayoutContainer, SettingsChangedListener {

    override val containerView: View get() = mainActivity.filterSheet

    private lateinit var behavior: BottomSheetBehavior<FrameLayout>
    private val callback: FilterBottomSheetCallback = FilterBottomSheetCallback()

    fun onCreate() {
        behavior = BottomSheetBehavior.from(mainActivity.filterSheet)
        hide()
        behavior.addBottomSheetCallback(callback)

        updateViewStates()

        filterEnabledSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked != Settings.filterEnabled) {
                Settings.commit { filterEnabled = isChecked }
            }
        }

        patternButton.setOnClickListener {
            PreferenceActivity
                .launch(mainActivity, ManagePatternRulesFragment.ACTION)
            hide()
        }
        moreSettingsButton.setOnClickListener {
            PreferenceActivity
                .launch(mainActivity, FilterSettingsFragment.ACTION)
            hide()
        }
        doneFilterButton.setOnClickListener {
            hide()
        }

        mainActivity.eventsHelper.registerListener(this)
    }

    fun onDestroy() {
        if (::behavior.isInitialized) {
            behavior.removeBottomSheetCallback(callback)
        }
        mainActivity.eventsHelper.unregisterListener(this)
    }

    private fun updateViewStates() {
        mainActivity.launchWhenCreated {
            filterEnabledSwitch.isChecked = Settings.filterEnabled
            filterPatternText.text = mainActivity.database.patternRules().getSelected().pattern
        }
    }

    fun onApplyWindowInsets(insets: WindowInsets) {
        filterSheetButtonBar.updatePadding(
            bottom = insets.systemWindowInsetBottom
        )
    }

    fun isShowing(): Boolean {
        return behavior.state == BottomSheetBehavior.STATE_EXPANDED
    }

    fun show() {
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    fun hide() {
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    inner class FilterBottomSheetCallback : BottomSheetBehavior.BottomSheetCallback() {

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (mainActivity.drawerLayout.isDrawerVisible(GravityCompat.START)) {
                return
            }
            if (slideOffset > -0.8F) {
                if (!mainActivity.lightNavBar) {
                    mainActivity.setWindowFlags(lightNavBar = true)
                }
                mainActivity.fab.hide()
            } else {
                if (mainActivity.lightNavBar) {
                    mainActivity.setWindowFlags(lightNavBar = false)
                }
                mainActivity.fab.show()
            }
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> {
                    mainActivity.fab.hide()
                }
                BottomSheetBehavior.STATE_HIDDEN -> {
                    mainActivity.fab.show()
                }
                BottomSheetBehavior.STATE_COLLAPSED -> {}
                BottomSheetBehavior.STATE_DRAGGING -> {}
                BottomSheetBehavior.STATE_HALF_EXPANDED -> {}
                BottomSheetBehavior.STATE_SETTLING -> {}
            }
        }

    }

    override fun onSettingsChanged() {
        updateViewStates()
    }

}