package moe.feng.danmaqua.ui.list

import android.animation.Animator
import android.animation.ObjectAnimator
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RaisedViewScrollListener(
    val targetView: View,
    raisedElevation: Float,
    startElevation: Float,
    val duration: Long,
    val targetViewDirection: Boolean? = null
) : RecyclerView.OnScrollListener() {

    private val statedElevation: Map<Boolean, Float> = mapOf(
        true to raisedElevation,
        false to startElevation
    )

    private var elevationAnimator: Animator? = null
    private var animationDirection: Boolean? = null

    private val elevationInState = object {
        operator fun get(state: Boolean): Float {
            return statedElevation[state] ?: error("statedElevation is not initialized.")
        }
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager !is LinearLayoutManager) {
            throw IllegalStateException("${javaClass.simpleName} requires LinearLayoutManager.")
        }
        var targetViewDirection = this.targetViewDirection
        if (targetViewDirection == null) {
            val targetViewLoc = IntArray(2).apply(targetView::getLocationInWindow)
            val recyclerViewLoc = IntArray(2).apply(recyclerView::getLocationInWindow)
            targetViewDirection = targetViewLoc[1] < recyclerViewLoc[1]
        }

        val shouldRaise = if (targetViewDirection) {
            layoutManager.findFirstCompletelyVisibleItemPosition() > 0
        } else {
            layoutManager.findLastCompletelyVisibleItemPosition() < layoutManager.itemCount - 1
        }

        if (animationDirection == null || animationDirection != shouldRaise) {
            if (elevationAnimator?.isRunning == true) {
                elevationAnimator?.cancel()
            }
        }
        animationDirection = shouldRaise
        if (elevationAnimator?.isRunning != true) {
            if (targetView.elevation != elevationInState[shouldRaise]) {
                elevationAnimator = ObjectAnimator.ofFloat(
                    targetView,
                    "elevation",
                    elevationInState[!shouldRaise],
                    elevationInState[shouldRaise]
                ).also {
                    it.duration = duration
                    it.start()
                }
            }
        }
    }

}