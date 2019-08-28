package pw.aru.testbot

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import pw.aru.psi.commands.Category
import pw.aru.psi.commands.Command
import pw.aru.psi.commands.ICategory
import pw.aru.psi.commands.ICommand
import pw.aru.psi.commands.context.CommandContext
import pw.aru.psi.commands.manager.CommandRegistry
import pw.aru.utils.extensions.lib.field

@Command("help")
@Category("enum#HELPFUL")
class HelpCommand(override val category: ICategory, override val kodein: Kodein) : ICommand, KodeinAware {
    override fun CommandContext.call() {
        val registry: CommandRegistry by instance()

        with(registry) {
            sendEmbed {
                categoryCommandsLookup.forEach { (key, value) ->
                    field(
                        categoryNameLookup[key] ?: "null",
                        value.joinToString { "`${commandLookup[it]?.first()}`" }
                    )
                }
            }
        }
    }
}
