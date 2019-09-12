package pw.aru.psi.commands

import pw.aru.psi.commands.manager.CommandRegistry

interface IRegistryInjector {
    fun inject(r: CommandRegistry)
}
