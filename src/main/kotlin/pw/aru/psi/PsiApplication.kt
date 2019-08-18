package pw.aru.psi

import org.kodein.di.direct
import org.kodein.di.generic.instance
import pw.aru.psi.bootstrap.BootstrapCreator
import pw.aru.psi.bootstrap.BootstrapLogger
import pw.aru.psi.bootstrap.BootstrapLogic
import pw.aru.psi.bootstrap.ShutdownManager
import kotlin.system.exitProcess

/**
 * PsiApplication -- the main class to start a bot application.
 *
 * @constructor Creates a instance with the bot definition.
 * @param def the bot definition.
 */
class PsiApplication(private val def: BotDef) {
    private lateinit var shutdownManager: ShutdownManager

    /**
     * Starts the bot application.
     */
    fun init() {
        val log = BootstrapLogger(def)
        val creator = BootstrapCreator(def)
        log.started()

        try {
            val scanResult = creator.scanResult()
            val catnip = creator.catnip()
            val kodein = creator.kodein(catnip)
            shutdownManager = kodein.direct.instance()

            BootstrapLogic(def, log, scanResult, catnip, kodein).launch()
        } catch (e: Exception) {
            log.failed(e)
            exitProcess(1)
        }
    }

    /**
     * Shutdowns a previously started bot application.
     */
    fun shutdown() {
        shutdownManager.shutdown()
    }
}
