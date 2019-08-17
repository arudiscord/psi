package pw.aru.testbot

import pw.aru.psi.commands.Category
import pw.aru.psi.commands.Command
import pw.aru.psi.commands.ICommand
import pw.aru.psi.commands.context.CommandContext

@Command("about")
class AboutCommand : ICommand {
    override val category: Category? = null

    override fun CommandContext.call() {
        sendEmbed {
            description("<3 for using our bot.")
        }
    }
}
