package moe.feng.danmaqua.model

typealias Subtitle = Pair<String?, String>

val Subtitle.who: String? get() = first

val Subtitle.text: String get() = second

fun Subtitle.flattenToString(): String = buildString {
    if (who?.isNotEmpty() == true) {
        append("$who：")
    }
    append(text)
}
