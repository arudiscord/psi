package pw.aru.psi

import com.mewna.catnip.Catnip
import com.mewna.catnip.extension.AbstractExtension
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import pw.aru.psi.bootstrap.BootstrapLogger
import pw.aru.psi.bootstrap.PsiBootstrap
import pw.aru.utils.KodeinExtension
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.system.exitProcess

/**
 * PsiApplication -- the main class to start a bot application.
 *
 * @constructor Creates a instance with the bot definition.
 * @param def the bot definition.
 */
class PsiApplication(private val def: BotDef) : AbstractExtension("psiApplication"), KodeinAware {
    private val shutdownListeners = CopyOnWriteArrayList<() -> Unit>()

    /**
     * Starts the bot application.
     */
    fun init() {
        val log = BootstrapLogger(def)
        log.started()

        try {
            val bootstrap = PsiBootstrap(this, def, log)
            bootstrap.launch()
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

    override fun catnip(): Catnip {
        return checkNotNull(super.catnip()) { NOT_INIT }
    }

    override val kodein: Kodein
        get() = checkNotNull(catnip().extension(KodeinExtension::class.java)) { NOT_INIT }.kodein

    companion object {
        const val NOT_INIT = "Application not initialized yet. Please call PsiApplication#init before"
    }
}
