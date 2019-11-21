package pw.aru.psi.bootstrap

import com.mewna.catnip.Catnip
import org.kodein.di.DKodein
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.eagerSingleton
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import pw.aru.libs.kodein.jit.installJit
import pw.aru.psi.BotDef
import pw.aru.psi.commands.manager.CommandProcessor
import pw.aru.psi.commands.manager.CommandRegistry
import pw.aru.psi.commands.manager.CommandRegistryImpl
import pw.aru.psi.executor.service.JavaThreadTaskExecutor
import pw.aru.psi.executor.service.TaskExecutorService
import pw.aru.psi.logging.DiscordLogger

/**
 * The framework's [Kodein] configurator.
 */
class PsiKodein(def: BotDef) : Kodein by Kodein(allowSilentOverride = true, init = {
    installJit()

    bind<Kodein>() with singleton { kodein }
    bind<DKodein>() with singleton { dkodein }

    bind<BotDef>() with instance(def)
    bind<Catnip>() with eagerSingleton { Catnip.catnip(def.catnipOptions) }

    bind<CommandRegistry>() with singleton { CommandRegistryImpl() }
    bind<CommandProcessor>() with singleton { CommandProcessor(instance()) }

    bind<TaskExecutorService>() with singleton { JavaThreadTaskExecutor.default }
    bind<ErrorHandler>() with singleton { ErrorHandler.Default }
    def.consoleWebhook?.let { bind<DiscordLogger>() with singleton { DiscordLogger(it) } }
    def.kodeinModule?.let { import(it, true) }
})