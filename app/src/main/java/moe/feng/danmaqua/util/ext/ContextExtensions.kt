package moe.feng.danmaqua.util.ext

import android.content.Context
import moe.feng.common.eventshelper.EventsHelper

val Context.eventsHelper: EventsHelper get() = EventsHelper.getInstance(this)
