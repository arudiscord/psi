package pw.aru.psi.commands

/**
 * ### [RegistryBootstrap][pw.aru.psi.bootstrap.RegistryBootstrap] annotation
 *
 * [ICategory] classes annotated with this class will be injected at the
 * [RegistryBootstrap][pw.aru.psi.bootstrap.RegistryBootstrap] and registered at the
 * [CommandRegistry][pw.aru.psi.commands.manager.CommandRegistry].
 *
 * Alternatively, [ICategory] enum classes annotated with this class will get all
 * the enum constants registered at the [CommandRegistry][pw.aru.psi.commands.manager.CommandRegistry],
 * with the name `${annotation.value}#${enum.name.toLowerCase()}`
 *
 * [ICommand] and [IRegistryInjector] classes annotated this class will be injected
 * with the [ICategory] of the value set in this annotation.
 *
 * @param value the command's names to register into the command registry.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Category(val value: String)