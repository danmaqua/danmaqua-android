package moe.feng.danmaqua.event

import moe.feng.common.eventshelper.EventsListener
import moe.feng.common.eventshelper.EventsOnThread

@EventsListener
interface NoConnectionsDialogListener {

    @EventsOnThread
    fun onIgnore()

}