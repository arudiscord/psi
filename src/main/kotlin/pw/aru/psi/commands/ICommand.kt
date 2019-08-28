package pw.aru.psi.commands

import com.mewna.catnip.entity.message.Embed
import com.mewna.catnip.entity.message.Message
import pw.aru.psi.BotDef
import pw.aru.psi.commands.ICommand.CustomHandler.Result
import pw.aru.psi.commands.context.CommandContext
import pw.aru.psi.permissions.Permissions

interface ICommand {
    val category: ICategory?

    fun CommandContext.call()

    fun nsfw(): Boolean {
        return category?.nsfw ?: false
    }

    interface Discrete : ICommand {
        fun CommandContext.discreteCall(outer: String)
    }

    interface Permission : ICommand {
        val permissions: Permissions
    }

    interface ExceptionHandler : ICommand {
        fun handle(message: Message, t: Throwable)
    }

    interface HelpDialog {
        fun onHelp(def: BotDef, message: Message): Embed
    }

    interface HelpDialogProvider {
        val helpHandler: HelpDialog
    }

    interface CustomHandler : ICommand {
        enum class Result {
            IGNORE, HANDLED
        }

        fun CommandContext.customCall(command: String): Result
    }

    interface CustomDiscreteHandler : ICommand {
        fun CommandContext.customCall(command: String, outer: String): Result
    }
}