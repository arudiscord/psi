@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LangExt")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang

import java.util.concurrent.Semaphore

inline fun <T> Semaphore.acquiring(permits: Int = 1, run: () -> T): T {
    acquire(permits)
    try {
        return run()
    } finally {
        release(permits)
    }
}
