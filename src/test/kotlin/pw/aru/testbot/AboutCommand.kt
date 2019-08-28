package pw.aru.testbot

import pw.aru.psi.commands.Category
import pw.aru.psi.commands.Command
import pw.aru.psi.commands.ICategory
import pw.aru.psi.commands.ICommand
import pw.aru.psi.commands.context.CommandContext

@Command("about", "thanks")
@Category("info")
class AboutCommand(override val category: ICategory) : ICommand {
    override fun CommandContext.call() {
        sendEmbed {
            description("<3 for using our bot.")
        }
    }
}
