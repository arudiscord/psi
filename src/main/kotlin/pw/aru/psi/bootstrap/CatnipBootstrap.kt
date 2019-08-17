package pw.aru.psi.bootstrap

import com.mewna.catnip.Catnip
import com.mewna.catnip.shard.DiscordEvent
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.instance
import pw.aru.psi.BotDef
import pw.aru.psi.commands.manager.CommandProcessor
import pw.aru.utils.KodeinExtension

class CatnipBootstrap(private val def: BotDef, private val kodein: Kodein) {
    var onFirstShardReady: () -> Unit = {}
    var onAllShardsReady: (Int) -> Unit = {}

    private val disposableRefs = ArrayList<Disposable>()

    init {
        val shutdownManager: ShutdownManager by kodein.instance()
        shutdownManager += { disposableRefs.forEach(Disposable::dispose) }
    }

    fun configure(catnip: Catnip) {
        catnip.loadExtension(KodeinExtension(kodein))

        disposableRefs += catnip.observable(DiscordEvent.MESSAGE_CREATE)
            .subscribe(kodein.direct.instance<CommandProcessor>(), def.bootstrap.errorHandler())

        val shardCount by lazy { catnip.gatewayInfo()!!.shards() }
        var ready = 0

        disposableRefs += catnip.observable(DiscordEvent.READY).subscribe {
            if (ready == 0) {
                //queue("onFirstShardReady", onFirstShardReady)
                onFirstShardReady()
            }

            if (++ready == shardCount) {
                //queue("onAllShardsReady") { onAllShardsReady(shardCount) }
                onAllShardsReady(shardCount)
            }
        }

        val guildLogger = GuildLogger(def.serversWebhook)

        disposableRefs += catnip.observable(DiscordEvent.GUILD_CREATE).subscribeBy(
            onNext = guildLogger::onGuildJoin,
            onError = def.bootstrap.errorHandler()::accept
        )

        disposableRefs += catnip.observable(DiscordEvent.GUILD_DELETE).subscribeBy(
            onNext = guildLogger::onGuildLeave,
            onError = def.bootstrap.errorHandler()::accept
        )

        catnip.connect()
    }

}