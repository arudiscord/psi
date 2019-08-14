package pw.aru.core

import com.mewna.catnip.entity.user.Presence.Activity
import com.mewna.catnip.entity.user.Presence.ActivityType.PLAYING
import com.mewna.catnip.entity.user.Presence.OnlineStatus.ONLINE
import com.mewna.catnip.entity.user.Presence.of
import io.github.classgraph.ClassGraph
import org.kodein.di.generic.instance
import pw.aru.core.bootstrap.BootstrapLogger
import pw.aru.core.bootstrap.CatnipBootstrap
import pw.aru.core.bootstrap.CommandBootstrap
import pw.aru.core.bootstrap.KodeinBootstrap
import pw.aru.core.commands.manager.CommandRegistry
import pw.aru.utils.AruTaskExecutor.task
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

@Suppress("unused")
class Bootstrap(private val def: BotDef) {
    fun load() {
        Locale.setDefault(Locale("en", "US"))

        val log = BootstrapLogger(def)
        log.started()

        try {
            val scanResult = ClassGraph()
                .enableClassInfo()
                .enableAnnotationInfo()
                .whitelistPackages("pw.aru", def.basePackage)
                .scan()

            val catnipBootstrap = CatnipBootstrap(def)

            val catnip = catnipBootstrap.create()

            val kodein = KodeinBootstrap(def, catnip).create()

            catnipBootstrap.run {
                onFirstShardReady = {
                    val commandBootstrap = CommandBootstrap(scanResult, kodein)

                    commandBootstrap.createCommands()
                    commandBootstrap.createProviders()
                    commandBootstrap.createStandalones()

                    scanResult.close()
                    commandBootstrap.reportResults()
                }

                onAllShardsReady = {
                    task(1, TimeUnit.MINUTES) {
                        val text = "${def.prefixes.first()}help | ${def.splashes.random()}"
                        catnip.presence(of(ONLINE, Activity.of(text, PLAYING)))
                    }

                    val registry by kodein.instance<CommandRegistry>()
                    log.successful(it, registry.commands.size)
                }

                configure(catnip, kodein)
            }
        } catch (e: Exception) {
            log.failed(e)
            exitProcess(1)
        }
    }
}
