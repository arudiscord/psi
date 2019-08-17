package pw.aru.core

import com.mewna.catnip.CatnipOptions
import io.reactivex.functions.Consumer
import org.kodein.di.Kodein
import java.awt.Color

interface BotDef {
    val botName: String
    val version: String
    val basePackage: String
    val prefixes: List<String>
    val splashes: List<String>

    val consoleWebhook: String // should be a discord webhook
    val serversWebhook: String // should be a discord webhook

    val mainColor: Color
    val bootstrap: BootstrapCallbacks
    val catnipOptions: CatnipOptions
    val kodeinModule: Kodein.Module?

    interface BootstrapCallbacks {
        fun handleError(throwable: Throwable): String // should return a link
        fun errorHandler(): Consumer<Throwable>
    }
}