package pw.aru.psi.commands.manager

import com.mewna.catnip.entity.guild.Member
import com.mewna.catnip.entity.message.Message
import com.mewna.catnip.entity.util.Permission.ADMINISTRATOR
import com.mewna.catnip.entity.util.Permission.SEND_MESSAGES
import io.reactivex.functions.Consumer
import mu.KLogging
import pw.aru.psi.BotDef
import pw.aru.psi.commands.ICommand
import pw.aru.psi.commands.ICommand.CustomHandler.Result.HANDLED
import pw.aru.psi.commands.context.CommandContext
import pw.aru.psi.parser.Args
import pw.aru.psi.permissions.Permission
import pw.aru.utils.PsiTaskExecutor.queue
import pw.aru.utils.extensions.lang.anyOf
import pw.aru.utils.extensions.lang.limit
import java.util.*

@Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")
open class CommandProcessor(
    protected val def: BotDef, protected val registry: CommandRegistry
) : Consumer<Message> {

    var count: Long = 0
        private set

    override fun accept(message: Message) {
        val self = message.guild()?.selfMember() ?: return

        if (!anyOf(
                message.author().bot(),
                !self.hasPermissions(message.channel().asGuildChannel(), SEND_MESSAGES),
                !self.hasPermissions(ADMINISTRATOR)
            )) return

        queue("Cmd:${message.author().discordTag()}:${message.content().limit(32)}") {
            onMessage(message)
        }
    }

    private fun onMessage(message: Message) {
        val raw = message.content()

        for (prefix in def.prefixes) {
            if (raw.startsWith(prefix)) {
                message.nextCommand(raw.substring(prefix.length).trimStart(), null)
                return
            }
        }

        val customPrefixes = customPrefixes(message)

        for (prefix in customPrefixes) {
            if (raw.startsWith(prefix)) {
                message.nextCommand(raw.substring(prefix.length).trimStart(), null)
                return
            }
        }

        if (raw.startsWith('[') && raw.contains(']')) {
            val (cmdRaw, cmdOuter) = raw.substring(1).trimStart().split(']', limit = 2)

            for (prefix in def.prefixes) {
                if (cmdRaw.startsWith(prefix)) {
                    message.nextCommand(cmdRaw.substring(prefix.length).trimStart(), cmdOuter)
                    return
                }
            }

            for (prefix in customPrefixes) {
                if (cmdRaw.startsWith(prefix)) {
                    message.nextCommand(cmdRaw.substring(prefix.length).trimStart(), cmdOuter)
                    return
                }
            }
        }
    }

    private fun Message.nextCommand(rawContent: String, outer: String?) {
        if (!filterMessages(this)) return

        val permissions = resolvePermissions(member()!!)
        if (permissions.isEmpty()) return // blacklisted

        val args = Args(rawContent)
        val cmd = args.takeString().toLowerCase()

        val command = registry[cmd]?.let { if (outer == null) it else it as? ICommand.Discrete }
        val ctx = CommandContext(this, args, permissions)

        if (command != null) {
            if (!filterCommands(this, command, permissions)) return
            beforeCommand(this, command)
            logger.trace { "Executing: $cmd by ${author().discordTag()} at ${Date()}" }
            count++
            if (outer == null) {
                command.runCatching { ctx.call() }
                    .onFailure { runCatching { onCommandError(command, this, it) } }
            } else {
                (command as ICommand.Discrete).runCatching { ctx.discreteCall(outer) }
                    .onFailure { runCatching { onCommandError(command, this, it) } }
            }
        } else {
            if (outer == null) {
                if (
                    registry.lookup.keys.mapNotNull { it as? ICommand.CustomHandler }.any {
                        it.runCatching { ctx.customCall(cmd) }.getOrNull() == HANDLED
                    }
                ) return
            } else {
                if (
                    registry.lookup.keys.mapNotNull { it as? ICommand.CustomDiscreteHandler }.any {
                        it.runCatching { ctx.customCall(cmd, outer) }.getOrNull() == HANDLED
                    }
                ) return
            }

            customHandleCommands(this, cmd, args, outer, permissions)
        }
    }


    private fun onCommandError(command: ICommand, message: Message, t: Throwable) {
        when {
            t == CommandContext.ShowHelp -> {
                if (command is ICommand.HelpDialogProvider) {
                    message.channel().sendMessage(command.helpHandler.onHelp(def, message))
                    return
                }

                if (command is ICommand.HelpDialog) {
                    message.channel().sendMessage(command.onHelp(def, message))
                    return
                }

                handleException(command, message, t, null)
            }

            command is ICommand.ExceptionHandler -> {
                try {
                    command.handle(message, t)
                } catch (u: Exception) {
                    handleException(command, message, t, u)
                }
            }

            else -> {
                handleException(command, message, t, null)
            }
        }
    }

    // extra stuff

    companion object : KLogging() {
        val dummyPermission = object : Permission {
            override val name = "Run Bot"
            override val description = "Override CommandProcessor#resolvePermissions to change this."
        }
    }

    // hooks

    protected fun customPrefixes(message: Message): List<String> = emptyList()

    protected fun filterMessages(message: Message): Boolean = true

    protected fun resolvePermissions(member: Member): Set<Permission> = setOf(dummyPermission)

    protected fun filterCommands(message: Message, command: ICommand, permissions: Set<Permission>) = true

    protected fun beforeCommand(message: Message, command: ICommand) = Unit

    protected fun customHandleCommands(
        message: Message, command: String, args: Args, outer: String?, permissions: Set<Permission>
    ) = Unit

    protected fun handleException(command: ICommand, message: Message, throwable: Throwable, underlying: Exception?) {
        underlying?.let(throwable::addSuppressed)
        throwable.printStackTrace()
    }
}