@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LangExt")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang

inline fun anyOf(vararg cases: Boolean) = cases.any { it }

inline fun allOf(vararg cases: Boolean) = cases.all { it }

inline fun noneOf(vararg cases: Boolean) = cases.none { it }
