package pw.aru.psi

import com.mewna.catnip.CatnipOptions
import org.kodein.di.Kodein
import java.awt.Color

interface BotDef {
    val botName: String
    val version: String
    val basePackage: String
    val prefixes: List<String>
    val splashes: List<String>

    val consoleWebhook: String? // should be a discord webhook
    val serversWebhook: String? // should be a discord webhook

    val mainColor: Color
    val catnipOptions: CatnipOptions
    val kodeinModule: Kodein.Module?
}