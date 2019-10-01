package pw.aru.ktbot

import pw.aru.psi.commands.Category
import pw.aru.psi.commands.Command
import pw.aru.psi.commands.ICategory
import pw.aru.psi.commands.ICommand
import pw.aru.psi.commands.context.CommandContext
import pw.aru.psi.exported.psi_version
import pw.aru.utils.extensions.lib.description

@Command("about", "thanks")
@Category("kt#info")
class AboutCommand(override val category: ICategory) : ICommand {
    override fun CommandContext.call() {
        sendEmbed {
            description(
                "This is an example Kotlin bot made using psi $psi_version.",
                "Thanks for using it <3."
            )
        }
    }
}
