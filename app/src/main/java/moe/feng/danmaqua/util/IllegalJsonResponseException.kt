package moe.feng.danmaqua.util

import okhttp3.Response
import java.lang.RuntimeException

class IllegalJsonResponseException : RuntimeException {

    val response: Response

    constructor(response: Response) : super() {
        this.response = response
    }

    constructor(message: String, response: Response) : super(message) {
        this.response = response
    }

}