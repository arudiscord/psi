package pw.aru.core

import com.mewna.catnip.CatnipOptions
import com.mewna.catnip.entity.guild.Member
import com.mewna.catnip.entity.message.Message
import io.reactivex.functions.Consumer
import org.kodein.di.Kodein
import pw.aru.core.commands.ICommand
import pw.aru.core.permissions.Permission
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

    override val commandProcessor: BotDef.ProcessorCallbacks = object : BotDef.ProcessorCallbacks {
        override fun getGuildPrefix(message: Message): String? = null

        override fun resolvePerms(message: Member): Set<Permission> = setOf(dummyPermission)

        override fun checkBotPermissions(message: Message): Boolean = true

        override fun runChecks(message: Message, command: ICommand, userPerms: Set<Permission>): Boolean = true

        override fun beforeCommand(message: Message, command: String) = Unit

        override fun handleExceptions(command: ICommand, message: Message, throwable: Throwable, underlying: Throwable?) {
            underlying?.let(throwable::addSuppressed)
            throwable.printStackTrace()
        }

        override fun handleCustomCommands(message: Message, cmd: String, args: String, userPerms: Set<Permission>) = Unit

        override fun handleDiscreteCustomCommands(message: Message, cmd: String, args: String, outer: String, userPerms: Set<Permission>) = Unit
    }

    private val dummyPermission = object : Permission {
        override val name: String = "Use Bot"
        override val description: String = "<3"
    }
}

fun main() {
    BotApplication(TestBot).init()
}