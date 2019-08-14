@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("Extensions")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang


fun String.initials(): String = filter(Char::isUpperCase)

fun String.limit(size: Int): String {
    return if (length <= size) this else substring(0, size - 3) + "..."
}
