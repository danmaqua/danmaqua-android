package moe.feng.danmaqua.util.ext

import android.util.Log
import com.tencent.mmkv.MMKV
import moe.feng.danmaqua.util.JsonUtils
import java.lang.Exception
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun MMKV.intProperty(key: String? = null, defaultValue: Int = 0) =
    IntProperty(this, key, defaultValue)

fun MMKV.booleanProperty(key: String? = null, defaultValue: Boolean = false) =
    BooleanProperty(this, key, defaultValue)

fun MMKV.notnullStringProperty(key: String? = null, defaultValue: String) =
    NotNullStringProperty(this, key, defaultValue)

fun MMKV.nullableStringProperty(key: String? = null, defaultValue: String? = null) =
    NullableStringProperty(this, key, defaultValue)

fun <T : Any> MMKV.jsonArrayProperty(key: String? = null,
                                     defaultJson: String = "[]",
                                     arrayClass: Class<Array<T>>) =
    JsonArrayProperty(this, key, defaultJson, arrayClass)

inline fun <reified T : Any> MMKV.jsonArrayProperty(
    key: String? = null, defaultJson: String = "[]") =
    jsonArrayProperty(key, defaultJson, Array<T>::class.java)

class IntProperty(
    val mmkv: MMKV,
    val key: String?,
    val defaultValue: Int
) : ReadWriteProperty<Any, Int> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Int {
        return mmkv.decodeInt(key ?: property.name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Int) {
        mmkv.encode(key ?: property.name, value)
    }

}

class BooleanProperty(
    val mmkv: MMKV,
    val key: String?,
    val defaultValue: Boolean
) : ReadWriteProperty<Any, Boolean> {

    override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
        return mmkv.decodeBool(key ?: property.name, defaultValue)
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) {
        mmkv.encode(key ?: property.name, value)
    }

}

class NotNullStringProperty(
    val mmkv: MMKV,
    val key: String?,
    val defaultValue: String
) : ReadWriteProperty<Any, String> {

    override fun getValue(thisRef: Any, property: KProperty<*>): String {
        return mmkv.decodeString(key ?: property.name) ?: defaultValue
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String) {
        mmkv.encode(key ?: property.name, value)
    }

}

class NullableStringProperty(
    val mmkv: MMKV,
    val key: String?,
    val defaultValue: String?
) : ReadWriteProperty<Any, String?> {

    override fun getValue(thisRef: Any, property: KProperty<*>): String? {
        return mmkv.decodeString(key ?: property.name) ?: defaultValue
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: String?) {
        mmkv.encode(key ?: property.name, value)
    }

}

class JsonArrayProperty<T : Any>(
    val mmkv: MMKV,
    val key: String?,
    val defaultJson: String,
    val arrayClass: Class<Array<T>>
) : ReadWriteProperty<Any, List<T>> {

    override fun getValue(thisRef: Any, property: KProperty<*>): List<T> {
        val key = key ?: property.name
        val json = mmkv.decodeString(key) ?: defaultJson
        try {
            return JsonUtils.fromJson(json, arrayClass).toList()
        } catch (e: Exception) {
            Log.e("MMKVExtension", "Cannot decode $key to json array. Fallback to empty array.")
        }
        return emptyList()
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: List<T>) {
        mmkv.encode(key ?: property.name, JsonUtils.toJson(value))
    }

}