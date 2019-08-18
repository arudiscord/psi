package pw.aru.psi.bootstrap

import com.mewna.catnip.Catnip
import com.mewna.catnip.entity.user.Presence
import com.mewna.catnip.shard.DiscordEvent
import io.github.classgraph.ScanResult
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.instance
import pw.aru.psi.BotDef
import pw.aru.psi.commands.manager.CommandProcessor
import pw.aru.psi.commands.manager.CommandRegistry
import pw.aru.psi.executor.service.TaskExecutorService
import pw.aru.utils.KodeinExtension
import java.util.concurrent.TimeUnit

class BootstrapLogic(
    private val def: BotDef,
    private val log: BootstrapLogger,
    private val scanResult: ScanResult,
    private val catnip: Catnip,
    private val kodein: Kodein
) {
    private val disposableRefs = ArrayList<Disposable>()

    init {
        val shutdownManager: ShutdownManager by kodein.instance()
        shutdownManager += { disposableRefs.forEach(Disposable::dispose) }
        shutdownManager += catnip::shutdown
    }

    fun launch() {
        catnip.loadExtension(KodeinExtension(kodein))

        val errorHandler: CatnipErrorHandler by kodein.instance()

        disposableRefs += catnip.observable(DiscordEvent.MESSAGE_CREATE)
            .subscribe(kodein.direct.instance<CommandProcessor>(), Consumer(errorHandler.onCommandProcessor()))

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
            onError = errorHandler.onReady()
        )

        def.serversWebhook?.let {
            val guildLogger = GuildLogger(def, it)

            disposableRefs += catnip.observable(DiscordEvent.GUILD_CREATE).subscribeBy(
                onNext = guildLogger::onGuildJoin,
                onError = errorHandler.onGuildSubscriptions()
            )

            disposableRefs += catnip.observable(DiscordEvent.GUILD_DELETE).subscribeBy(
                onNext = guildLogger::onGuildLeave,
                onError = errorHandler.onGuildSubscriptions()
            )
        }

        catnip.connect()
    }

    private fun onFirstShardReady() {
        val commandBootstrap = CommandBootstrap(scanResult, kodein)

        commandBootstrap.createCommands()
        commandBootstrap.createProviders()
        commandBootstrap.createStandalones()

        scanResult.close()
        commandBootstrap.reportResults()
    }

    private fun onAllShardsReady(shardCount: Int) {
        def.splashes.let { splashes ->
            when (splashes.size) {
                0 -> {
                    val text = "${def.prefixes.first()}help}"
                    catnip.presence(Presence.of(Presence.OnlineStatus.ONLINE, Presence.Activity.of(text, Presence.ActivityType.PLAYING)))
                }
                1 -> {
                    val text = "${def.prefixes.first()}help | ${splashes.single()}"
                    catnip.presence(Presence.of(Presence.OnlineStatus.ONLINE, Presence.Activity.of(text, Presence.ActivityType.PLAYING)))
                }
                else -> {
                    val tasks by kodein.instance<TaskExecutorService>()
                    tasks.task(1, TimeUnit.MINUTES) {
                        val text = "${def.prefixes.first()}help | ${splashes.random()}"
                        catnip.presence(Presence.of(Presence.OnlineStatus.ONLINE, Presence.Activity.of(text, Presence.ActivityType.PLAYING)))
                    }
                }
            }
        }

        val registry by kodein.instance<CommandRegistry>()
        log.successful(shardCount, registry.commands.size)
    }
}