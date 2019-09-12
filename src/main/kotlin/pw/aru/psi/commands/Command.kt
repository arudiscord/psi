package pw.aru.psi.commands

/**
 * ### [RegistryBootstrap][pw.aru.psi.bootstrap.RegistryBootstrap] annotation
 *
 * [ICommand] classes annotated with this class will be injected at the
 * [RegistryBootstrap][pw.aru.psi.bootstrap.RegistryBootstrap] and registered at the
 * [CommandRegistry][pw.aru.psi.commands.manager.CommandRegistry].

 * @param value the command's names to register into the command registry.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Command(vararg val value: String)

