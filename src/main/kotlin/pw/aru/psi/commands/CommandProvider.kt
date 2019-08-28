package pw.aru.psi.commands

/**
 * ### [CommandBootstrap][pw.aru.psi.bootstrap.CommandBootstrap] annotation
 *
 * [ICommandProvider] classes annotated with this class will be injected at the
 * [CommandBootstrap][pw.aru.psi.bootstrap.CommandBootstrap] and the [ICommandProvider.provide]
 * method will be injected with the [CommandRegistry][pw.aru.psi.commands.manager.CommandRegistry].
 *
 * @param value the command's names to register into the command registry.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class CommandProvider