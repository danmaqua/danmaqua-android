package moe.feng.danmaqua.event

import moe.feng.common.eventshelper.EventsListener
import moe.feng.common.eventshelper.EventsOnThread
import moe.feng.common.eventshelper.EventsOnThread.MAIN_THREAD
import moe.feng.danmaqua.model.Recommendation

@EventsListener
interface OnRecommendedStreamerItemClickListener {

    @EventsOnThread(MAIN_THREAD)
    fun onRecommendedStreamerItemClick(item: Recommendation.Item)

}