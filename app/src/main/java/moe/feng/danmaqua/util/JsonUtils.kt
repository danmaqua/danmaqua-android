package moe.feng.danmaqua.util

import com.google.gson.Gson

object JsonUtils {

    var gson: Gson = Gson()

    fun toJson(obj: Any): String {
        return gson.toJson(obj)
    }

    fun <T> fromJson(string: String, clazz: Class<T>): T {
        return gson.fromJson(string, clazz)
    }

    inline fun <reified T> fromJson(string: String): T {
        return fromJson(string, T::class.java)
    }

}