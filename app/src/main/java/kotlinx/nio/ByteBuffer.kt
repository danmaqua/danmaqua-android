package kotlinx.nio

import java.nio.ByteBuffer

fun ByteBuffer.getUInt(offset: Int): UInt {
    return this.getInt(offset).toUInt()
}

fun ByteBuffer.slice(position: Int): ByteBuffer {
    return (this.duplicate().position(position) as ByteBuffer).slice()
}

fun ByteBuffer.slice(position: Int, limit: Int): ByteBuffer {
    return (this.duplicate().position(position).limit(limit) as ByteBuffer).slice()
}

fun ByteBuffer.currentSliceArray(): ByteArray {
    val byteArray = ByteArray(this.capacity())
    this.get(byteArray, 0, this.capacity())
    return byteArray
}
