package moe.feng.danmaqua.model

fun Recommendation.Item.toSubscription() = Subscription(uid, room, name, face)

fun VTuberSingleItem.toSubscription() = Subscription(uid, room, name, face)
