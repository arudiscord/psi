package pw.aru.core.commands.manager

import mu.KLogging
import pw.aru.core.commands.ICommand
import pw.aru.utils.extensions.lang.classOf

class CommandRegistry {
    interface Listener {
        fun unnamedCommand(command: ICommand)

        fun noHelpCommand(command: ICommand, names: List<String>)

        fun multipleHelpCommand(command: ICommand, names: List<String>)
    }

    companion object : KLogging() {
        private val helpInterfaces = listOf(
            classOf<ICommand.HelpDialogProvider>(),
            classOf<ICommand.HelpDialog>()
        )

        val NOOP_LISTENER = object : Listener {
            override fun unnamedCommand(command: ICommand) = Unit

            override fun noHelpCommand(command: ICommand, names: List<String>) = Unit

            override fun multipleHelpCommand(command: ICommand, names: List<String>) = Unit
        }
    }

    val commands: MutableMap<String, ICommand> = LinkedHashMap()
    val lookup: MutableMap<ICommand, MutableList<String>> = LinkedHashMap()
    var listener = NOOP_LISTENER

    operator fun get(key: String) = commands[key]

    operator fun set(vararg names: String, command: ICommand) {
        register(names.toList(), command)
    }

    fun register(names: List<String>, command: ICommand) {
        if (!sanityChecks(command, names)) return

        val keys = names.asSequence()
            .map(String::toLowerCase)
            .distinct()
            .onEach { commands[it] = command }

        lookup.getOrPut(command, ::ArrayList).addAll(keys)
    }

    private fun sanityChecks(command: ICommand, names: List<String>): Boolean {
        if (names.isEmpty()) {
            listener.unnamedCommand(command)
            return false
        }

        val implemented = helpInterfaces.filter { it.isInstance(command) }

        if (implemented.isEmpty()) {
            listener.noHelpCommand(command, names)
        } else if (implemented.size > 1) {
            listener.multipleHelpCommand(command, names)
        }

        return true
    }
}
