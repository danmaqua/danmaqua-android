package moe.feng.danmaqua.event

import moe.feng.common.eventshelper.EventsListener
import moe.feng.common.eventshelper.EventsOnThread
import moe.feng.common.eventshelper.EventsOnThread.MAIN_THREAD

@EventsListener
interface SettingsChangedListener {

    @EventsOnThread(MAIN_THREAD)
    fun onSettingsChanged()

}