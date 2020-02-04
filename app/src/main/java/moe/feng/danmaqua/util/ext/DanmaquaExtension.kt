package moe.feng.danmaqua.util.ext

import android.content.Context
import moe.feng.danmaqua.DanmaquaApplication
import moe.feng.danmaqua.data.DanmaquaDB

fun Context.getDanmaquaDatabase(): DanmaquaDB {
    return DanmaquaApplication.getDatabase(this)
}
