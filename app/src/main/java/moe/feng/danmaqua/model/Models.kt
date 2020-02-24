package moe.feng.danmaqua.model

import java.util.*
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun Recommendation.Item.toSubscription() = Subscription(uid, room, name, face)

fun VTuberSingleItem.toSubscription() = Subscription(uid, room, name, face)

fun buildTextTranslation(block: TextTranslation.() -> Unit): TextTranslation {
    return TextTranslation().apply(block)
}

private class TextTranslationProperty(
    private val locale: Locale
) : ReadWriteProperty<TextTranslation, String?> {

    override fun getValue(thisRef: TextTranslation, property: KProperty<*>): String? {
        return thisRef[locale]
    }

    override fun setValue(thisRef: TextTranslation, property: KProperty<*>, value: String?) {
        thisRef[locale] = value
    }

}

var TextTranslation.english: String? by TextTranslationProperty(Locale.ENGLISH)

var TextTranslation.chinese: String? by TextTranslationProperty(Locale.CHINESE)
