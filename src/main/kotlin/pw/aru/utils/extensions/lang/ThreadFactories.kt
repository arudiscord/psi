@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LangExt")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.thread

fun threadFactory(
    isDaemon: Boolean = false,
    contextClassLoader: ClassLoader? = null,
    nameFormat: String? = null,
    priority: Int = -1
): ThreadFactory {
    val count = if (nameFormat != null) AtomicLong(0) else null
    return ThreadFactory {
        thread(false, isDaemon, contextClassLoader, nameFormat?.format(count!!.getAndIncrement()), priority, it::run)
    }
}

fun threadGroupBasedFactory(
    name: String,
    isDaemon: Boolean = false,
    contextClassLoader: ClassLoader? = null,
    priority: Int = -1
): ThreadFactory {
    val group = ThreadGroup(name)
    val count = AtomicLong(0)

    return ThreadFactory {
        Thread(group, it, "$name-${count.getAndIncrement()}").also { thread ->
            if (isDaemon) thread.isDaemon = true
            if (priority > 0) thread.priority = priority
            if (contextClassLoader != null) thread.contextClassLoader = contextClassLoader
        }
    }
}
