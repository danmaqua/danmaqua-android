package moe.feng.danmaqua.model

import androidx.core.os.LocaleListCompat
import androidx.core.os.forEach
import java.util.*

open class TextTranslation : HashMap<String, String?>() {

    private fun getByKey(block: (key: String) -> Boolean): String? {
        return keys.find(block)?.let { get(it) }
    }

    operator fun invoke(): String? {
        return this[LocaleListCompat.getDefault()]
    }

    operator fun get(localeList: LocaleListCompat): String? {
        if (isNotEmpty()) {
            localeList.getFirstMatch(keys.toTypedArray())?.let {
                return get(it, false)
            }
            localeList.forEach {
                val result = get(it, false)
                if (result != null) {
                    return result
                }
            }
            return get(if (localeList.isEmpty) {
                Locale.getDefault()
            } else {
                get(localeList[0])
            })
        }
        return null
    }

    operator fun set(locale: Locale, text: String?) {
        put(Locale(locale.language, locale.country).toString().replace('_', '-'), text)
    }

    operator fun get(locale: Locale): String? {
        return get(locale, true)
    }

    fun get(locale: Locale, allowDefault: Boolean): String? {
        if (isNotEmpty()) {
            val matchAll = Locale(locale.language, locale.country)
            val matchLangOnly = Locale(locale.language)
            return getByKey { it.replace('-', '_') == matchAll.toString() }
                ?: getByKey { it == matchLangOnly.toString() }
                ?: getByKey { it.startsWith(matchLangOnly.toString()) }
                ?: if (allowDefault) {
                    get("en") ?: values.firstOrNull()
                } else {
                    null
                }
        }
        return null
    }

}