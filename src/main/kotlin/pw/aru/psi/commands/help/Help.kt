package pw.aru.psi.commands.help

import com.mewna.catnip.entity.message.Embed
import com.mewna.catnip.entity.message.Message
import pw.aru.psi.BotDef
import pw.aru.psi.commands.ICommand
import pw.aru.psi.permissions.Permissions
import pw.aru.utils.extensions.lib.embed
import pw.aru.utils.extensions.lib.field
import java.awt.Color

class Help(
    private val d: BaseDescription,
    vararg val nodes: HelpNode
) : ICommand.HelpDialog {
    override fun onHelp(def: BotDef, message: Message): Embed = embed {
        val names: List<String>?
        val title: String
        val color: Color
        val permissions: Permissions?
        val thumbnail: String

        when (d) {
            is CommandDescription -> {
                names = d.names
                title = d.title
                color = d.color ?: def.mainColor
                permissions = d.permissions
                thumbnail = d.thumbnail
            }
            is CategoryDescription -> {
                names = null
                title = d.title
                color = d.color ?: def.mainColor
                permissions = d.permissions
                thumbnail = d.thumbnail
            }
        }

        color(color)
        thumbnail(thumbnail)

        author(title, null, message.catnip().selfUser()?.effectiveAvatarUrl())
        footer(
            "Requested by ${message.member()!!.effectiveName()}",
            message.author().effectiveAvatarUrl()
        )

        if (permissions != null) {
            field("Permissions Required:", permissions.toString().capitalize())
        }

        if (names != null && names.size > 1) {
            field("Aliases:", names.asSequence().drop(1).joinToString("` `", "`", "`"))
        }

        for (node in nodes) when (node) {
            is Description -> field("Description:", node.value)
            is Usage -> field("Usage:", node.value(def.prefixes.first()))
            is Example -> field(
                "Example:",
                node.value(def.prefixes.first())
            )
            is Note -> field("Note:", node.value)
            is SeeAlso -> field("See Also:", node.value)

            is Field -> field(node.name, node.value)
        }
    }
}
