package pw.aru.testbot

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import pw.aru.psi.commands.Category
import pw.aru.psi.commands.Command
import pw.aru.psi.commands.ICommand
import pw.aru.psi.commands.context.CommandContext
import pw.aru.psi.commands.manager.CommandRegistry

@Command("help")
class HelpCommand(override val kodein: Kodein) : ICommand, KodeinAware {
    override val category: Category? = null

    private val registry: CommandRegistry by instance()

    override fun CommandContext.call() {
        sendEmbed {
            description("**Commands**: ${registry.lookup.values.joinToString { "`${it.first()}`" }}")
        }
    }
}
