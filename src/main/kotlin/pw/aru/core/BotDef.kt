package pw.aru.core

import com.mewna.catnip.CatnipOptions
import com.mewna.catnip.entity.guild.Member
import com.mewna.catnip.entity.message.Message
import io.reactivex.functions.Consumer
import org.kodein.di.Kodein
import pw.aru.core.commands.ICommand
import pw.aru.core.permissions.Permission
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
    val commandProcessor: ProcessorCallbacks

    interface BootstrapCallbacks {
        fun handleError(throwable: Throwable): String // should return a link
        fun errorHandler(): Consumer<Throwable>
    }

    interface ProcessorCallbacks {
        fun getGuildPrefix(message: Message): String?

        fun resolvePerms(message: Member): Set<Permission> // return a empty set to blacklist

        fun checkBotPermissions(message: Message): Boolean

        fun runChecks(message: Message, command: ICommand, userPerms: Set<Permission>): Boolean

        fun beforeCommand(message: Message, command: String)

        fun handleExceptions(command: ICommand, message: Message, throwable: Throwable, underlying: Throwable?)

        fun handleCustomCommands(message: Message, cmd: String, args: String, userPerms: Set<Permission>)

        fun handleDiscreteCustomCommands(
            message: Message,
            cmd: String,
            args: String,
            outer: String,
            userPerms: Set<Permission>
        )
    }
}