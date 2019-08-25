@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LangExt")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang

fun String.initials(): String = filter(Char::isUpperCase)

fun String.limit(size: Int): String {
    return if (length <= size) this else substring(0, size - 3) + "..."
}

inline fun multiline(vararg lines: String) = lines.joinToString("\n")

inline fun Any.format(s: String): String = s.format(this)

inline operator fun Appendable.plusAssign(other: CharSequence) {
    append(other)
}

inline operator fun Appendable.plusAssign(other: Char) {
    append(other)
}
