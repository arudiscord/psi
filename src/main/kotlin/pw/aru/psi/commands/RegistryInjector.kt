package pw.aru.psi.commands

/**
 * ### [RegistryBootstrap][pw.aru.psi.bootstrap.RegistryBootstrap] annotation
 *
 * [IRegistryInjector] classes annotated with this class will be injected at the
 * [RegistryBootstrap][pw.aru.psi.bootstrap.RegistryBootstrap] and the [IRegistryInjector.provide]
 * method will be injected with the [CommandRegistry][pw.aru.psi.commands.manager.CommandRegistry].
 *
 * @param value the phase which this registry extension should be injected and initialized.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class RegistryInjector(val value: RegistryPhase)