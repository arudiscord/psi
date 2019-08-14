package pw.aru.core.commands.manager

import com.mewna.catnip.entity.message.Message
import mu.KLogging
import pw.aru.core.BotDef
import pw.aru.core.commands.ICommand
import pw.aru.core.commands.ICommand.ExceptionHandler
import pw.aru.core.commands.context.CommandContext
import pw.aru.core.commands.context.CommandContext.ShowHelp
import pw.aru.core.parser.Args.Companion.SPLIT_CHARS
import pw.aru.core.permissions.Permission
import java.util.*
import kotlin.collections.component1
import kotlin.collections.component2

class CommandProcessor(
    private val def: BotDef,
    private val registry: CommandRegistry
) : KLogging() {

    var commandCount = 0

    fun onCommand(message: Message) {
        val raw = message.content()

        for (prefix in def.prefixes) {
            if (raw.startsWith(prefix)) {
                process(message, raw.substring(prefix.length).trimStart())
                return
            }
        }

        val guildPrefix: String? = def.commandProcessor.getGuildPrefix(message)

        if (guildPrefix != null && raw.startsWith(guildPrefix)) {
            process(message, raw.substring(guildPrefix.length))
            return
        }

        // onDiscreteCommand(message)
        if (raw.startsWith('[') && raw.contains(']')) {
            val (cmdRaw, cmdOuter) = raw.substring(1).trimStart().split(']', limit = 2)

            for (prefix in def.prefixes) {
                if (cmdRaw.startsWith(prefix)) {
                    processDiscrete(message, cmdRaw.substring(prefix.length).trimStart(), cmdOuter)
                    return
                }
            }

            if (guildPrefix != null && cmdRaw.startsWith(guildPrefix)) {
                processDiscrete(message, cmdRaw.substring(guildPrefix.length), cmdOuter)
                return
            }
        }
    }

    private fun process(message: Message, content: String) {
        if (!def.commandProcessor.checkBotPermissions(message)) return

        val userPerms = def.commandProcessor.resolvePerms(message.member()!!)
        if (userPerms.isEmpty()) return // Global Blacklist

        val split = content.split(*SPLIT_CHARS, limit = 2)
        val cmd = split[0].toLowerCase()
        val args = split.getOrNull(1)?.trimStart(*SPLIT_CHARS) ?: ""

        val command = registry[cmd] ?: return processCustomCommand(message, cmd, args, userPerms)

        if (!def.commandProcessor.runChecks(message, command, userPerms)) return

        def.commandProcessor.beforeCommand(message, cmd)

        logger.trace {
            "Command invoked: $cmd, by ${message.author().discordTag()} with timestamp ${Date()}"
        }

        runCommand(command, message, args, userPerms)
    }

    private fun processCustomCommand(message: Message, cmd: String, args: String, userPerms: Set<Permission>) {
        val ctx = CommandContext(message, args, userPerms)

        if (
            registry.lookup.keys.mapNotNull { it as? ICommand.CustomHandler }.any {
                it.runCatching { ctx.customCall(cmd) }.getOrNull() == ICommand.CustomHandler.Result.HANDLED
            }
        ) return

        def.commandProcessor.handleCustomCommands(message, cmd, args, userPerms)
    }

    private fun processDiscreteCustomCommand(
        message: Message,
        cmd: String,
        args: String,
        outer: String,
        userPerms: Set<Permission>
    ) {
        val ctx = CommandContext(message, args, userPerms)

        if (
            registry.lookup.keys.mapNotNull { it as? ICommand.CustomDiscreteHandler }.any {
                it.runCatching { ctx.customCall(cmd, outer) }.getOrNull() == ICommand.CustomHandler.Result.HANDLED
            }
        ) return

        def.commandProcessor.handleDiscreteCustomCommands(message, cmd, args, outer, userPerms)
    }

    private fun runCommand(command: ICommand, message: Message, args: String, userPerms: Set<Permission>) {
        commandCount++

        command.runCatching {
            CommandContext(message, args, userPerms).call()
        }.onFailure { runCatching { handleException(command, message, it) } }
    }

    private fun processDiscrete(message: Message, content: String, outer: String) {
        if (!def.commandProcessor.checkBotPermissions(message)) return

        val userPerms = def.commandProcessor.resolvePerms(message.member()!!)
        if (userPerms.isEmpty()) return // Global Blacklist

        val split = content.split(' ', limit = 2)
        val cmd = split[0].toLowerCase()
        val args = split.getOrNull(1) ?: ""

        val command = registry[cmd] as? ICommand.Discrete ?: return processDiscreteCustomCommand(
            message,
            cmd,
            args,
            outer,
            userPerms
        )

        if (!def.commandProcessor.runChecks(message, command, userPerms)) return

        def.commandProcessor.beforeCommand(message, cmd)

        runDiscreteCommand(command, message, args, outer, userPerms)

        logger.trace {
            "Discrete Command invoked: $cmd, by ${message.author().discordTag()} with timestamp ${Date()}"
        }
    }

    private fun runDiscreteCommand(
        command: ICommand.Discrete,
        message: Message,
        args: String,
        outer: String,
        userPerms: Set<Permission>
    ) {
        commandCount++

        command.runCatching {
            CommandContext(message, args, userPerms).discreteCall(outer)
        }.onFailure { runCatching { handleException(command, message, it) } }
    }

    private fun handleException(command: ICommand, message: Message, t: Throwable) {
        when {
            t == ShowHelp -> {
                if (command is ICommand.HelpDialogProvider) {
                    message.channel().sendMessage(command.helpHandler.onHelp(def, message))
                    return
                }

                if (command is ICommand.HelpDialog) {
                    message.channel().sendMessage(command.onHelp(def, message))
                    return
                }

                throw ShowHelp
            }

            command is ExceptionHandler -> {
                try {
                    command.handle(message, t)
                } catch (u: Exception) {
                    def.commandProcessor.handleExceptions(command, message, t, u)
                }
            }

            else -> {
                def.commandProcessor.handleExceptions(command, message, t, null)
            }
        }
    }
}