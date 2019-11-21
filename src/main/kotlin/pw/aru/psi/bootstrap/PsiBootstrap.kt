package pw.aru.psi.bootstrap

import com.mewna.catnip.Catnip
import com.mewna.catnip.entity.user.Presence.ActivityType.PLAYING
import com.mewna.catnip.entity.user.Presence.OnlineStatus.ONLINE
import com.mewna.catnip.shard.DiscordEvent
import io.github.classgraph.ClassGraph
import io.github.classgraph.ScanResult
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy
import org.kodein.di.KodeinAware
import org.kodein.di.direct
import org.kodein.di.generic.instance
import pw.aru.libs.eventpipes.api.EventPipe
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
    private val eventPipe: EventPipe<PsiApplicationEvent>
) : KodeinAware {
    override val kodein = PsiKodein(def)

    private val scanResult: ScanResult
        by ClassGraph()
            .enableClassInfo()
            .enableAnnotationInfo()
            .whitelistPackages("pw.aru", def.basePackage)
            .scanAsync(direct.instance<TaskExecutorService>().asJavaExecutor(), runtime().availableProcessors())

    private val disposableRefs = ArrayList<Disposable>()

    private val catnip: Catnip by instance()

    init {
        app.registerShutdownHook { disposableRefs.forEach(Disposable::dispose) }
        app.registerShutdownHook(catnip::shutdown)
    }

    fun launch() {
        catnip.loadExtension(KodeinExtension(kodein)).loadExtension(app)

        val errorHandler: ErrorHandler by instance()

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

        def.serversWebhook?.let {
            val guildLogger = GuildLogger(def, it)

            disposableRefs += catnip.observable(DiscordEvent.GUILD_CREATE).subscribeBy(
                onNext = guildLogger::onGuildJoin,
                onError = errorHandler::onGuildSubscriptions
            )

            disposableRefs += catnip.observable(DiscordEvent.GUILD_DELETE).subscribeBy(
                onNext = guildLogger::onGuildLeave,
                onError = errorHandler::onGuildSubscriptions
            )
        }

        catnip.connect()
    }

    private fun onFirstShardReady() {
        with(RegistryBootstrap(scanResult, kodein)) {
            eventPipe.publish(BeforeRegistryPhaseEvent(app, RegistryPhase.PRE_INITIALIZATION))
            loadInjectors(RegistryPhase.PRE_INITIALIZATION)
            eventPipe.publish(AfterRegistryPhaseEvent(app, RegistryPhase.PRE_INITIALIZATION))
            createCategories()
            eventPipe.publish(BeforeRegistryPhaseEvent(app, RegistryPhase.AFTER_CATEGORIES))
            loadInjectors(RegistryPhase.AFTER_CATEGORIES)
            eventPipe.publish(AfterRegistryPhaseEvent(app, RegistryPhase.AFTER_CATEGORIES))
            createCommands()
            eventPipe.publish(BeforeRegistryPhaseEvent(app, RegistryPhase.AFTER_COMMANDS))
            loadInjectors(RegistryPhase.AFTER_COMMANDS)
            eventPipe.publish(AfterRegistryPhaseEvent(app, RegistryPhase.AFTER_COMMANDS))
            createStandalones()
            eventPipe.publish(BeforeRegistryPhaseEvent(app, RegistryPhase.AFTER_EXECUTABLES))
            loadInjectors(RegistryPhase.AFTER_EXECUTABLES)
            eventPipe.publish(AfterRegistryPhaseEvent(app, RegistryPhase.AFTER_EXECUTABLES))
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
        eventPipe.publish(ApplicationStartedEvent(app))
    }

    private fun presence(vararg parts: String?) {
        val text = parts.asSequence().filterNotNull().joinToString(" | ")

        if (text.isNotEmpty()) {
            catnip.presence(presenceOf(ONLINE, activityOf(text, PLAYING)))
        }
    }
}