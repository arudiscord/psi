package pw.aru.ktbot

import com.mewna.catnip.CatnipOptions
import org.kodein.di.Kodein
import pw.aru.psi.BotDef
import pw.aru.psi.PsiApplication
import pw.aru.utils.Colors

object KtBot : BotDef {
    override val botName = "KtBot"
    override val version = "1.0"
    override val basePackage = "pw.aru.ktbot"
    override val prefixes = listOf("!")
    override val splashes = listOf("Kotlin!")
    override val consoleWebhook = System.getenv("webhook")
    override val serversWebhook = System.getenv("webhook")
    override val mainColor = Colors.discordPurple

    override val catnipOptions = CatnipOptions(System.getenv("token"))
    override val kodeinModule: Kodein.Module? = null
}

fun main() {
    PsiApplication(KtBot).init()
}