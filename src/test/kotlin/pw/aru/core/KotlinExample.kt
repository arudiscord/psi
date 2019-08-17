package pw.aru.core

import com.mewna.catnip.CatnipOptions
import io.reactivex.functions.Consumer
import org.kodein.di.Kodein
import pw.aru.utils.Colors
import java.awt.Color

object TestBot : BotDef {
    override val botName: String = "TestBot"
    override val version: String = "1.0"
    override val basePackage: String = "pw.aru.core"
    override val prefixes: List<String> = listOf("!")
    override val splashes: List<String> = listOf("I love tests!")
    override val consoleWebhook: String = System.getenv("webhook")
    override val serversWebhook: String = System.getenv("webhook")
    override val mainColor: Color = Colors.blurple

    override val bootstrap: BotDef.BootstrapCallbacks = object : BotDef.BootstrapCallbacks {
        override fun handleError(throwable: Throwable): String {
            throwable.printStackTrace()
            return "<check your logs>"
        }

        override fun errorHandler(): Consumer<Throwable> {
            return Consumer(Throwable::printStackTrace)
        }
    }

    override val catnipOptions: CatnipOptions = CatnipOptions(System.getenv("token"))

    override val kodeinModule: Kodein.Module? = null
}

fun main() {
    BotApplication(TestBot).init()
}