package pw.aru.core

import pw.aru.core.commands.Category
import pw.aru.core.commands.Command
import pw.aru.core.commands.ICommand
import pw.aru.core.commands.context.CommandContext

@Command("help")
class HelpCommand : ICommand {
    override val category: Category? = null

    override fun CommandContext.call() {
        sendEmbed {
            description("<3 for using our bot.")
        }
    }

}
