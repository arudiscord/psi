package pw.aru.psi.bootstrap

import java.util.concurrent.CopyOnWriteArrayList

class ShutdownManager {
    private val listeners = CopyOnWriteArrayList<() -> Unit>()

    operator fun plusAssign(listener: () -> Unit) {
        listeners += listener
    }

    fun shutdown() {
        listeners.forEach { runCatching(it) }
    }
}