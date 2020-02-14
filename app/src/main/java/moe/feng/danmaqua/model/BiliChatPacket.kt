package moe.feng.danmaqua.model

import moe.feng.danmaqua.api.bili.Protocol
import moe.feng.danmaqua.util.ext.toJson

class BiliChatPacket(
    val operation: Int,
    val protocol: Int,
    val data: Any
) {

    operator fun component1(): Int {
        return operation
    }

    operator fun component2(): Int {
        return protocol
    }

    operator fun component3(): Any {
        return data
    }

    override fun equals(other: Any?): Boolean {
        if (other is BiliChatPacket) {
            return this.operation == other.operation &&
                    this.protocol == other.protocol &&
                    if (this.protocol == Protocol.PROTOCOL_JSON && this.data !is String) {
                        this.data.toJson() == other.data.toJson()
                    } else {
                        this.data == other.data
                    }
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        var result = operation
        result = 31 * result + protocol
        result = 31 * result + data.hashCode()
        return result
    }

}