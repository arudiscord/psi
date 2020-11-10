package pw.aru.psi.commands.context

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.direct
import org.kodein.di.generic.instance
import pw.aru.psi.BotDef
import pw.aru.psi.parser.Args
import pw.aru.utils.extensions.lib.embed
import pw.aru.utils.extensions.lib.message

data class CommandContext(
    val message: Message,
    override val kodein: Kodein,
    val args: Args
) : KodeinAware {
    val jda: JDA by lazy { message.jda }

    val def by lazy { direct.instance<BotDef>() }

    val author = message.author

    val channel: MessageChannel
        get() = message.channel

    val self by lazy { jda.selfUser }

    fun sendEmbed(builder: EmbedBuilder = EmbedBuilder(), init: EmbedBuilder.() -> Unit) =
        channel.sendMessage(embed(builder, init)).submit()

    fun sendMessage(init: MessageBuilder.() -> Unit) = channel.sendMessage(message(init).build()).submit()

    fun send(text: String) = channel.sendMessage(text).submit()

    fun send(embed: MessageEmbed) = channel.sendMessage(embed).submit()

    fun send(message: Message) = channel.sendMessage(message).submit()

    fun showHelp(): Unit = throw ShowHelp

    fun <T> returnHelp(): T = throw ShowHelp

    object ShowHelp : RuntimeException()
}

