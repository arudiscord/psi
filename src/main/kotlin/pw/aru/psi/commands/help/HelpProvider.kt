package pw.aru.psi.commands.help

import com.mewna.catnip.entity.message.Embed
import com.mewna.catnip.entity.message.Message
import pw.aru.psi.BotDef

interface HelpProvider {
    fun onHelp(def: BotDef, message: Message): Embed
}