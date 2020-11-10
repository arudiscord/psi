package pw.aru.psi.commands.help

import net.dv8tion.jda.api.entities.Message
import pw.aru.psi.BotDef
import pw.aru.psi.commands.help.nodes.*
import pw.aru.utils.extensions.lib.embed
import pw.aru.utils.extensions.lib.field
import java.awt.Color
import java.util.*

class HelpEmbed(private val description: BaseDescription) : HelpProvider {
    companion object {
        fun command(
            names: List<String>,
            title: String,
            color: Color? = null,
            thumbnail: String? = null
        ) = HelpEmbed(CommandDescription(names, title, color, thumbnail))

        fun category(
            title: String,
            color: Color? = null,
            thumbnail: String? = null
        ) = HelpEmbed(CategoryDescription(title, color, thumbnail))
    }

    private val nodes = LinkedList<HelpNode>()

    fun addNode(node: HelpNode) = apply {
        nodes.add(node)
    }

    override fun onHelp(def: BotDef, message: Message) = embed {
        val names = when (description) {
            is CommandDescription -> description.names
            is CategoryDescription -> null
        }
        val title = description.title
        val color = description.color ?: def.mainColor
        val thumbnail = description.thumbnail

        // begin embed
        setColor(color)
        thumbnail?.let(::setThumbnail)

        setAuthor(title, null, message.jda.selfUser.effectiveAvatarUrl)
        setFooter("Requested by ${message.author.name}", message.author.effectiveAvatarUrl)

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