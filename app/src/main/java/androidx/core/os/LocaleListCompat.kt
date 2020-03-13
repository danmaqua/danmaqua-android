package androidx.core.os

import java.util.*

fun LocaleListCompat.iterable(): Iterable<Locale> {
    return Iterable {
        iterator()
    }
}

fun LocaleListCompat.iterator(): Iterator<Locale> {
    return iterator {
        forEach {
            yield(it)
        }
    }
}

inline fun LocaleListCompat.forEach(block: (locale: Locale) -> Unit) {
    for (i in 0 until size()) {
        block(get(i))
    }
}