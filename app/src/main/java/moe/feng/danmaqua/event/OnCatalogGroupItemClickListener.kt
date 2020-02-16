package moe.feng.danmaqua.event

import moe.feng.common.eventshelper.EventsListener
import moe.feng.common.eventshelper.EventsOnThread
import moe.feng.common.eventshelper.EventsOnThread.MAIN_THREAD
import moe.feng.danmaqua.model.VTuberCatalog

@EventsListener
interface OnCatalogGroupItemClickListener {

    @EventsOnThread(MAIN_THREAD)
    fun onCatalogItemClick(item: VTuberCatalog.Group)

}