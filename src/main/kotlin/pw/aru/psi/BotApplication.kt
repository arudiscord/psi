package pw.aru.psi

import com.mewna.catnip.Catnip
import com.mewna.catnip.entity.user.Presence.Activity
import com.mewna.catnip.entity.user.Presence.ActivityType.PLAYING
import com.mewna.catnip.entity.user.Presence.OnlineStatus.ONLINE
import com.mewna.catnip.entity.user.Presence.of
import io.github.classgraph.ClassGraph
import org.kodein.di.direct
import org.kodein.di.generic.instance
import pw.aru.psi.bootstrap.*
import pw.aru.psi.commands.manager.CommandRegistry
import pw.aru.psi.executor.TaskExecutorService
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

@Suppress("unused")
class BotApplication(private val def: BotDef) {
    private lateinit var shutdownManager: ShutdownManager

    fun init() {
        val log = BootstrapLogger(def)
        log.started()

        try {
            val scanResult = ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .whitelistPackages("pw.aru", def.basePackage)
                .scan()

            val catnip = Catnip.catnip(def.catnipOptions)

            val kodein = KodeinBootstrap(def, catnip).create()

            CatnipBootstrap(def, kodein).run {
                onFirstShardReady = {
                    val commandBootstrap = CommandBootstrap(scanResult, kodein)

                    commandBootstrap.createCommands()
                    commandBootstrap.createProviders()
                    commandBootstrap.createStandalones()

                    scanResult.close()
                    commandBootstrap.reportResults()
                }

                onAllShardsReady = {
                    val service by kodein.instance<TaskExecutorService>()
                    service.task(1, TimeUnit.MINUTES) {
                        val text = "${def.prefixes.first()}help | ${def.splashes.random()}"
                        catnip.presence(of(ONLINE, Activity.of(text, PLAYING)))
                    }

                    val registry by kodein.instance<CommandRegistry>()
                    log.successful(it, registry.commands.size)
                }

                configure(catnip)
            }

            shutdownManager = kodein.direct.instance()
        } catch (e: Exception) {
            log.failed(e)
            exitProcess(1)
        }
    }

    fun shutdown() {
        shutdownManager.shutdown()
    }
}
