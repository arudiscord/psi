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
import pw.aru.psi.commands.manager.CommandRegistryImpl
import pw.aru.utils.extensions.lang.limit
import pw.aru.utils.extensions.lib.field

@Command("debug")
@Category("enum#debug")
class DebugCommand(override val category: ICategory, override val kodein: Kodein) : ICommand, KodeinAware {
    override fun CommandContext.call() {
        val registry: CommandRegistry by instance()

        with(registry as CommandRegistryImpl) {
            sendEmbed {
                field(
                    "commands",
                    commands.toString().limit(1024)
                )
                field(
                    "categories",
                    categories.toString().limit(1024)
                )
                field(
                    "commandNameLookup",
                    commandNameLookup.toString().limit(1024)
                )
                field(
                    "categoryNameLookup",
                    categoryNameLookup.toString().limit(1024)
                )
                field(
                    "categoryCommandsLookup",
                    categoryCommandsLookup.toString().limit(1024)
                )
            }
        }
    }
}
