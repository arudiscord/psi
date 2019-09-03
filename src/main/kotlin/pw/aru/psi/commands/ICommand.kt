package pw.aru.psi.commands

import com.mewna.catnip.entity.message.Message
import pw.aru.psi.commands.ICommand.CustomHandler.Result
import pw.aru.psi.commands.context.CommandContext
import pw.aru.psi.commands.help.HelpProvider
import pw.aru.psi.permissions.Permissions

interface ICommand {
    val category: ICategory?
        get() = null

    val permissions: Permissions
        get() = Permissions.none

    val nsfw: Boolean
        get() = category?.nsfw ?: false

    val help: HelpProvider?
        get() = this as? HelpProvider

    fun CommandContext.call()

    interface Discrete : ICommand {
        fun CommandContext.discreteCall(outer: String)
    }

    interface ExceptionHandler : ICommand {
        fun handle(message: Message, t: Throwable)
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