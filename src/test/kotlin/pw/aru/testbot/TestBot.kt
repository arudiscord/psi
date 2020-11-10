package pw.aru.testbot

import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import org.kodein.di.Kodein
import pw.aru.psi.BotDef
import pw.aru.psi.PsiApplication
import pw.aru.utils.Colors

object TestBot : BotDef {
    override val botName = "TestBot"
    override val version = "1.0"
    override val basePackage = "pw.aru.testbot"
    override val prefixes = listOf("!")
    override val splashes = listOf("I love tests!")
    override val mainColor = Colors.blurple

    override val builder = DefaultShardManagerBuilder.createLight(
        System.getenv("token"), GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES
    )
    override val kodeinModule: Kodein.Module? = null
}

fun main() {
    PsiApplication(TestBot).init()
}