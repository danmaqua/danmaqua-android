package moe.feng.danmaqua.util.ext

import android.os.Parcel

fun Parcel.readBool(): Boolean {
    return readByte() != 0.toByte()
}

fun Parcel.writeBool(boolean: Boolean) {
    writeByte(if (boolean) 1.toByte() else 0.toByte())
}
