@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LangExt")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.simpleName(): String {
    var c: Class<*>? = javaClass

    while (c != null) {
        val name = c.simpleName
        if (name.isNotEmpty()) return name
        c = c.superclass
    }

    return "Throwable"
}

fun Throwable.especializationName(): String {
    var c: Class<*>? = javaClass

    while (c != null) {
        val name = c.simpleName
        if (name.isNotEmpty()) {
            return when {
                name.endsWith("Exception") -> name.substring(0, name.length - 9)
                name.endsWith("Error") -> name.substring(0, name.length - 5)
                else -> name
            }
        }
        c = c.superclass
    }

    return "Throwable"
}

fun Throwable.stackTraceToString(): String {
    return StringWriter().also { printStackTrace(PrintWriter(it, true)) }.toString()
}