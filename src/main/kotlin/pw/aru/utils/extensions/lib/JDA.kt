package pw.aru.utils.extensions.lib

import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.MessageChannel
import net.dv8tion.jda.api.entities.MessageEmbed
import net.dv8tion.jda.api.entities.User

val User.discordTag: String
    get() = "$name#$discriminator"

inline fun message(init: MessageBuilder.() -> Unit): MessageBuilder =
    MessageBuilder().also(init)

inline fun embed(embed: EmbedBuilder = EmbedBuilder(), init: EmbedBuilder.() -> Unit): MessageEmbed = embed.also(init).build()

inline fun MessageBuilder.embed(embed: EmbedBuilder = EmbedBuilder(), init: EmbedBuilder.() -> Unit) {
    setEmbed(embed.also(init).build())
}

inline fun MessageChannel.sendEmbed(embed: EmbedBuilder = EmbedBuilder(), init: EmbedBuilder.() -> Unit) =
    sendMessage(embed.also(init).build())

inline fun MessageChannel.sendMessage(init: MessageBuilder.() -> Unit) =
    sendMessage(message(init).build())

fun EmbedBuilder.footer(text: String) {
    setFooter(text, null)
}

fun EmbedBuilder.field(name: String, vararg value: String) {
    addField(name, value.joinToString("\n"), false)
}

fun EmbedBuilder.inlineField(name: String, vararg value: String) {
    addField(name, value.joinToString("\n"), true)
}

fun EmbedBuilder.description(vararg text: String) {
    setDescription(text.joinToString("\n"))
}

fun EmbedBuilder.blankField(inline: Boolean = false) {
    addBlankField(inline)
}
