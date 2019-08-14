package pw.aru.core.commands

import pw.aru.core.commands.manager.CommandRegistry

interface ICommandProvider {
    fun provide(r: CommandRegistry)
}
