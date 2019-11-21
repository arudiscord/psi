package pw.aru.psi

import com.mewna.catnip.Catnip
import com.mewna.catnip.extension.AbstractExtension
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import pw.aru.libs.eventpipes.EventPipes
import pw.aru.psi.bootstrap.*
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
    private val events = EventPipes.newAsyncPipe<PsiApplicationEvent>()

    /**
     * Starts the bot application.
     */
    fun init() {
        check(!init) { ALREADY_INIT }
        init = true

        events.publish(ApplicationStartedEvent(this))

        try {
            val bootstrap = PsiBootstrap(this, def, events)
            bootstrap.launch()
        } catch (e: Exception) {
            events.publish(FailedBootEvent(this, e))
            if (exitIfError) {
                exitProcess(1)
            }
        }
    }

    fun onEvents(block: (PsiApplicationEvent) -> Unit) = apply {
        events.subscribe(block)
    }

    inline fun <reified T : PsiApplicationEvent> onEvent(crossinline block: (T) -> Unit) = apply {
        onEvents {
            if (it is T) {
                block(it)
            }
        }
    }

    /**
     * Registers a shutdown hook.
     *
     * @param hook the shutdown hook
     */
    @Deprecated(message = "Replace with onEvents<BeforeShutdownEvent>")
    fun registerShutdownHook(hook: () -> Unit) = apply {
        events.subscribe {
            if (it is BeforeShutdownEvent) hook()
        }
    }

    /**
     * Shutdowns a previously started bot application.
     */
    fun shutdown() {
        check(init) { NOT_INIT }
        events.publish(BeforeShutdownEvent(this)).thenRunAsync {
            events.publish(ShutdownEvent(this))
        }
    }

    override fun catnip(): Catnip {
        return checkNotNull(super.catnip()) { NOT_INIT }
    }

    override val kodein: Kodein
        get() = checkNotNull(catnip().extension(KodeinExtension::class.java)) { NOT_INIT }.kodein

    private var init = false

    private var exitIfError = true

    fun dontExitIfError() = apply { exitIfError = true }

    companion object {
        const val NOT_INIT = "Application not initialized yet. Please call PsiApplication#init before"
        const val ALREADY_INIT = "Application already initialized!"
    }
}
