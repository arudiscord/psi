package pw.aru.core

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import pw.aru.core.commands.Category
import pw.aru.core.commands.Command
import pw.aru.core.commands.ICommand
import pw.aru.core.commands.context.CommandContext
import pw.aru.core.commands.manager.CommandRegistry

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
