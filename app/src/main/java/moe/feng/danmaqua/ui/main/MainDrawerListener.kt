package moe.feng.danmaqua.ui.main

import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import moe.feng.danmaqua.ui.MainActivity

class MainDrawerListener(private val mainActivity: MainActivity) : DrawerLayout.DrawerListener {

    override fun onDrawerStateChanged(newState: Int) {

    }

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        if (slideOffset > 0.5F) {
            if (!mainActivity.lightNavBar) {
                mainActivity.setWindowFlags(lightNavBar = true)
            }
        } else {
            if (mainActivity.lightNavBar) {
                mainActivity.setWindowFlags(lightNavBar = false)
            }
        }
    }

    override fun onDrawerClosed(drawerView: View) {
        mainActivity.setGestureExclusionEnabled(true)
    }

    override fun onDrawerOpened(drawerView: View) {
        mainActivity.setGestureExclusionEnabled(false)
    }

}