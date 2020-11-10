package pw.aru.psi.commands.help

import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageEmbed
import pw.aru.psi.BotDef

interface HelpProvider {
    fun onHelp(def: BotDef, message: Message): MessageEmbed
}