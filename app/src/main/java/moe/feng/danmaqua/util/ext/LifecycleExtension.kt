package moe.feng.danmaqua.util.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

val LifecycleOwner.isResumed get() = lifecycle.currentState == Lifecycle.State.RESUMED