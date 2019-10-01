package pw.aru.ktbot

import pw.aru.psi.commands.Category;
import pw.aru.psi.commands.Command;

@Command("help")
@Category("enum#helpful")
class HelpCommand(override val category:ICategory,override val kodein:Kodein):ICommand,KodeinAware{
    override fun CommandContext.call(){
    val registry:CommandRegistry by instance()

    sendEmbed{
    registry.categorizedCommands().forEach{(key,value)->
    field(
    key.categoryName,
    value.joinToString(" "){"`${registry.names(it)?.first()}`"}
    )
    }
    }
    }
    }
