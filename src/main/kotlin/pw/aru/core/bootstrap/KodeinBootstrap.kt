package pw.aru.core.bootstrap

import com.mewna.catnip.Catnip
import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import pw.aru.core.BotDef
import pw.aru.core.commands.manager.CommandProcessor
import pw.aru.core.commands.manager.CommandRegistry
import pw.aru.core.logging.DiscordLogger
import pw.aru.libs.kodein.jit.installJit

class KodeinBootstrap(private val def: BotDef, private val catnip: Catnip) {
    fun create() = Kodein {
        // Install JIT Module
        installJit()

        // Self-references
        bind<Kodein>() with singleton { kodein }
        bind<DKodein>() with singleton { dkodein }

        // Instances
        bind<BotDef>() with instance(def)
        bind<CommandRegistry>() with singleton { CommandRegistry() }
        bind<CommandProcessor>() with singleton { CommandProcessor(instance(), instance()) }
        bind<Catnip>() with instance(catnip)

        bind<DiscordLogger>() with singleton { DiscordLogger(def.consoleWebhook) }
        bind<ShutdownManager>() with singleton { ShutdownManager() }

        def.kodeinModule?.let { import(it, true) }
    }
}
