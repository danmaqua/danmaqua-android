package moe.feng.danmaqua.util.ext

import com.tencent.mmkv.MMKV
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