package moe.feng.danmaqua.util.ext

import moe.feng.danmaqua.util.JsonUtils

fun <T : Any> T.toJson(): String {
    return JsonUtils.toJson(this)
}
