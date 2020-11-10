package pw.aru.psi.commands.manager

import mu.KLogging
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.TextChannel
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import pw.aru.psi.BotDef
import pw.aru.psi.commands.ICommand
import pw.aru.psi.commands.ICommand.CustomHandler.Result.HANDLED
import pw.aru.psi.commands.context.CommandContext
import pw.aru.psi.executor.service.TaskExecutorService
import pw.aru.psi.parser.Args
import pw.aru.utils.extensions.lang.anyOf
import pw.aru.utils.extensions.lang.limit
import pw.aru.utils.extensions.lib.discordTag
import java.util.*
import java.util.function.Consumer

@Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")
open class CommandProcessor(override val kodein: Kodein) : Consumer<MessageReceivedEvent>, KodeinAware {
    protected val def by instance<BotDef>()
    protected val registry by instance<CommandRegistry>()
    protected val tasks by instance<TaskExecutorService>()

    var count: Long = 0
        private set

    override fun accept(event: MessageReceivedEvent) {
        val message = event.message

        val self = message.jda.selfUser

        if (!anyOf(message.author.isBot, (message.channel as? TextChannel)?.canTalk() != false)) return

        tasks.queue("Cmd:${message.author.discordTag}:${message.contentRaw.limit(32)}") {
            onMessage(message)
        }
    }

    private fun onMessage(message: Message) {
        val raw = message.contentRaw

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

        val args = Args(rawContent)
        val cmd = args.takeString().toLowerCase()

        val command = registry.command(cmd)?.let { if (outer == null) it else it as? ICommand.Discrete }
        val ctx = CommandContext(this, kodein, args)

        if (command != null) {
            beforeCommand(this, cmd, command)
            logger.trace { "Executing: $cmd by ${author.discordTag} at ${Date()}" }
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
                    registry.commands().mapNotNull { it as? ICommand.CustomHandler }.any {
                        it.runCatching { ctx.customCall(cmd) }.getOrNull() == HANDLED
                    }
                ) return
            } else {
                if (
                    registry.commands().mapNotNull { it as? ICommand.CustomDiscreteHandler }.any {
                        it.runCatching { ctx.customCall(cmd, outer) }.getOrNull() == HANDLED
                    }
                ) return
            }

            customHandleCommands(this, cmd, args, outer)
        }
    }

    // overrideable behaviour

    protected open fun onCommandError(command: ICommand, message: Message, t: Throwable) {
        when {
            t == CommandContext.ShowHelp -> {
                command.help?.let {
                    it.onHelp(def, message)
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

    // hooks

    protected open fun customPrefixes(message: Message): List<String> = emptyList()

    protected open fun filterMessages(message: Message): Boolean = true

    protected open fun beforeCommand(message: Message, cmd: String, command: ICommand) = Unit

    protected open fun customHandleCommands(
        message: Message, command: String, args: Args, outer: String?
    ) = Unit

    protected open fun handleException(command: ICommand, message: Message, throwable: Throwable, underlying: Throwable?) {
        underlying?.let(throwable::addSuppressed)
        logger.error(throwable) { "Error while executing $command" }
    }

    // extra stuff

    private companion object : KLogging()
}