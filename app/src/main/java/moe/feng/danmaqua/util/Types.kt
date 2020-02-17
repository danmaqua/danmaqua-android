package moe.feng.danmaqua.util

typealias Subtitle = Pair<String?, String>

val Subtitle.who: String? get() = first

val Subtitle.text: String get() = second

fun Subtitle.flattenToString(): String = buildString {
    if (who?.isNotEmpty() == true) {
        append("$who：")
    }
    append(text)
}
