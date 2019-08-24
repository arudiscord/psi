package pw.aru.psi.commands.context

import com.mewna.catnip.Catnip
import com.mewna.catnip.entity.builder.EmbedBuilder
import com.mewna.catnip.entity.channel.TextChannel
import com.mewna.catnip.entity.guild.Guild
import com.mewna.catnip.entity.message.Embed
import com.mewna.catnip.entity.message.Message
import com.mewna.catnip.entity.message.MessageOptions
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.direct
import org.kodein.di.generic.instance
import org.slf4j.MDC
import pw.aru.psi.BotDef
import pw.aru.psi.parser.Args
import pw.aru.psi.permissions.Permission
import pw.aru.utils.extensions.lib.embed
import pw.aru.utils.extensions.lib.message
import pw.aru.utils.kodein

data class CommandContext(
    val message: Message,
    val args: Args,
    val permissions: Set<Permission>
) : KodeinAware {
    val catnip: Catnip by lazy { message.catnip() }

    override val kodein: Kodein by lazy { catnip.kodein().kodein }

    val def by lazy { direct.instance<BotDef>() }

    val author by lazy { ContextMember(message.author(), message.member()!!) }

    val channel: TextChannel
        get() = message.channel().asTextChannel()

    val guild: Guild
        get() = message.guild()!!

    val self by lazy { ContextMember(catnip.selfUser()!!, guild.selfMember()) }

    @Deprecated(
        message = "Use CommandContext#args directly.",
        replaceWith = ReplaceWith("args"),
        level = DeprecationLevel.ERROR
    )
    fun parseable() = args

    fun showHelp(): Unit = throw ShowHelp

    fun <T> returnHelp(): T = throw ShowHelp

    fun sendEmbed(builder: EmbedBuilder = EmbedBuilder(), init: EmbedBuilder.() -> Unit) =
        channel.sendMessage(embed(builder, init))

    fun sendMessage(init: MessageOptions.() -> Unit) = channel.sendMessage(message(init))

    fun send(text: String) = channel.sendMessage(text)

    fun send(embed: Embed) = channel.sendMessage(embed)

    fun send(message: Message) = channel.sendMessage(message)

    fun <T, R> T.withMDC(vararg pairs: Pair<String, String>, block: T.() -> R) {
        for ((k, v) in pairs) MDC.put(k, v)
        try {
            block()
        } finally {
            for ((k) in pairs) MDC.remove(k)
        }
    }

    object ShowHelp : RuntimeException()
}

