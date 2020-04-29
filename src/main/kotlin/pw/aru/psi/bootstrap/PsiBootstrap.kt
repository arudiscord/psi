package pw.aru.psi.bootstrap

import com.mewna.catnip.Catnip
import com.mewna.catnip.entity.user.Presence.ActivityType.PLAYING
import com.mewna.catnip.entity.user.Presence.OnlineStatus.ONLINE
import com.mewna.catnip.shard.DiscordEvent
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.kotlin.subscribeBy
import org.kodein.di.KodeinAware
import org.kodein.di.direct
import org.kodein.di.generic.instance
import pw.aru.psi.BotDef
import pw.aru.psi.PsiApplication
import pw.aru.psi.commands.RegistryPhase
import pw.aru.psi.commands.manager.CommandProcessor
import pw.aru.psi.commands.manager.CommandRegistry
import pw.aru.psi.executor.service.TaskExecutorService
import pw.aru.psi.executor.service.asJavaExecutor
import pw.aru.utils.KodeinExtension
import pw.aru.utils.extensions.lang.getValue
import java.util.concurrent.TimeUnit
import com.mewna.catnip.entity.user.Presence.Activity.of as activityOf
import com.mewna.catnip.entity.user.Presence.of as presenceOf
import java.lang.Runtime.getRuntime as runtime

/**
 * Class that bootstraps the framework components.
 */
class PsiBootstrap(
    private val app: PsiApplication,
    private val def: BotDef,
    private val log: BootstrapLogger
) : KodeinAware {
    override val kodein = PsiKodein(def)

    private val scanResult: ScanResult
        by ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .whitelistPackages("pw.aru", def.basePackage)
            .scanAsync(direct.instance<TaskExecutorService>().asJavaExecutor(), runtime().availableProcessors())

    private val disposableRefs = ArrayList<Disposable>()

    private val catnip by instance<Catnip>()

    init {
        app.registerShutdownHook { disposableRefs.forEach(Disposable::dispose) }
        app.registerShutdownHook(catnip::shutdown)
    }

    fun launch() {
        catnip.loadExtension(KodeinExtension(kodein)).loadExtension(app)

        val errorHandler by instance<ErrorHandler>()

        disposableRefs += catnip.observable(DiscordEvent.MESSAGE_CREATE)
            .subscribe(direct.instance<CommandProcessor>(), Consumer(errorHandler::onCommandProcessor))

        val shardCount by lazy { catnip.gatewayInfo()!!.shards() }
        var ready = 0

        disposableRefs += catnip.observable(DiscordEvent.READY).subscribeBy(
            onNext = {
                if (ready == 0) {
                    //queue("onFirstShardReady", onFirstShardReady)
                    onFirstShardReady()
                }

                if (++ready == shardCount) {
                    //queue("onAllShardsReady") { onAllShardsReady(shardCount) }
                    onAllShardsReady(shardCount)
                }
            },
            onError = errorHandler::onReady
        )

        catnip.connect()
    }

    private fun onFirstShardReady() {
        with(RegistryBootstrap(scanResult, kodein)) {
            loadInjectors(RegistryPhase.PRE_INITIALIZATION)
            createCategories()
            loadInjectors(RegistryPhase.AFTER_CATEGORIES)
            createCommands()
            loadInjectors(RegistryPhase.AFTER_COMMANDS)
            createStandalones()
            loadInjectors(RegistryPhase.AFTER_EXECUTABLES)
        }

        scanResult.close()
    }

    private fun onAllShardsReady(shardCount: Int) {
        val splashes = def.splashes
        val mainCommand = def.prefixes.firstOrNull()?.let { def.mainCommandName?.let { name -> "$it$name" } }

        if (splashes.size > 1) {
            val tasks by kodein.instance<TaskExecutorService>()
            tasks.task(1, TimeUnit.MINUTES) {
                presence(mainCommand, splashes.random())
            }
        } else {
            presence(mainCommand, splashes.firstOrNull())
        }

        val registry by kodein.instance<CommandRegistry>()
        log.successful(shardCount, registry.categoryCount(), registry.commandCount())
    }

    private fun presence(vararg parts: String?) {
        val text = parts.asSequence().filterNotNull().joinToString(" | ")

        if (text.isNotEmpty()) {
            catnip.presence(presenceOf(ONLINE, activityOf(text, PLAYING)))
        }
    }
}