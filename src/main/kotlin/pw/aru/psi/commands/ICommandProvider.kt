package pw.aru.psi.commands

import pw.aru.psi.commands.manager.CommandRegistry

interface ICommandProvider {
    fun provide(r: CommandRegistry)
}
