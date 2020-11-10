package pw.aru.psi

import pw.aru.psi.bootstrap.BootstrapLogger
import pw.aru.psi.bootstrap.bootstrap
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.system.exitProcess

/**
 * PsiApplication -- the main class to start a bot application.
 *
 * @constructor Creates a instance with the bot definition.
 * @param def the bot definition.
 */
class PsiApplication(private val def: BotDef) {
    private val shutdownListeners = CopyOnWriteArrayList<() -> Unit>()

    /**
     * Starts the bot application.
     */
    fun init() {
        val log = BootstrapLogger(def)
        log.started()

        try {
            bootstrap(this, def, log)
        } catch (e: Exception) {
            log.failed(e)
            exitProcess(1)
        }
    }

    /**
     * Registers a shutdown hook.
     *
     * @param hook the shutdown hook
     */
    fun registerShutdownHook(hook: () -> Unit) = apply {
        shutdownListeners += hook
    }

    /**
     * Shutdowns a previously started bot application.
     *
     * @return the errors that happened while shutting down.
     */
    fun shutdown(): List<Throwable> {
        return shutdownListeners.mapNotNull { runCatching(it).exceptionOrNull() }
    }

    companion object {
        const val NOT_INIT = "Application not initialized yet. Please call PsiApplication#init before"
    }
}
