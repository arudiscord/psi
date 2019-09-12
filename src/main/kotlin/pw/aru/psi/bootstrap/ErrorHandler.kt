package pw.aru.psi.bootstrap

import org.slf4j.LoggerFactory
import pw.aru.psi.PsiApplication
import pw.aru.psi.commands.ICategory
import pw.aru.psi.commands.ICommand
import pw.aru.psi.commands.IRegistryInjector
import pw.aru.psi.commands.RegistryPhase
import pw.aru.psi.executor.Executable

/**
 * Error handler interface for the Psi framework.
 */
interface ErrorHandler {
    /**
     * Exception handler for [READY][com.mewna.catnip.shard.DiscordEvent.READY]  events.
     *
     * @param throwable the received throwable.
     */
    fun onReady(throwable: Throwable)

    /**
     * Exception handler for [MESSAGE_CREATE][com.mewna.catnip.shard.DiscordEvent.MESSAGE_CREATE] events.
     *
     * @param throwable the received throwable.
     */
    fun onCommandProcessor(throwable: Throwable)

    /**
     * Exception handler for [GUILD_CREATE][com.mewna.catnip.shard.DiscordEvent.GUILD_CREATE] and
     * [GUILD_DELETE][com.mewna.catnip.shard.DiscordEvent.GUILD_DELETE] events.
     *
     * @param throwable the received throwable.
     */
    fun onGuildSubscriptions(throwable: Throwable)

    /**
     * Exception handler for exceptions on [IRegistryInjector] creation.
     *
     * @param targetClass the target [IRegistryInjector] class.
     * @param phase the [RegistryPhase] of the injection.
     * @param throwable the received throwable.
     */
    fun onRegistryInjectorCreation(targetClass: Class<in IRegistryInjector>, phase: RegistryPhase, throwable: Throwable)

    /**
     * Exception handler for exceptions on [ICategory] creation.
     *
     * @param targetClass the target [ICategory] class.
     * @param throwable the received throwable.
     */
    fun onCategoryCreation(targetClass: Class<in ICategory>, throwable: Throwable)

    /**
     * Exception handler for exceptions on [ICommand] creation.
     *
     * @param targetClass the target [ICommand] class.
     * @param throwable the received throwable.
     */
    fun onCommandCreation(targetClass: Class<in ICommand>, throwable: Throwable)

    /**
     * Exception handler for exceptions on [Executable] creation.
     *
     * @param targetClass the target [Executable] class.
     * @param throwable the received throwable.
     */
    fun onExecutableCreation(targetClass: Class<in Executable>, throwable: Throwable)

    /**
     * The default [ErrorHandler] implementation, based on SLF4J.
     */
    object Default : ErrorHandler {
        private val logger = LoggerFactory.getLogger(PsiApplication::class.java)

        override fun onReady(throwable: Throwable) {
            logger.error("An error happened while setting up Catnip", throwable)
        }

        override fun onCommandProcessor(throwable: Throwable) {
            logger.error("An error happened while processing commands", throwable)
        }

        override fun onGuildSubscriptions(throwable: Throwable) {
            logger.error("An error happened while posting guild join/leaves", throwable)
        }

        override fun onRegistryInjectorCreation(targetClass: Class<in IRegistryInjector>, phase: RegistryPhase, throwable: Throwable) {
            logger.error("An error happened while creating registry injector ${targetClass.name} at phase $phase", throwable)
        }

        override fun onCategoryCreation(targetClass: Class<in ICategory>, throwable: Throwable) {
            logger.error("An error happened while creating category ${targetClass.name}", throwable)
        }

        override fun onCommandCreation(targetClass: Class<in ICommand>, throwable: Throwable) {
            logger.error("An error happened while creating command ${targetClass.name}", throwable)
        }

        override fun onExecutableCreation(targetClass: Class<in Executable>, throwable: Throwable) {
            logger.error("An error happened while creating executable ${targetClass.name}", throwable)
        }
    }
}