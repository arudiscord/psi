package pw.aru.psi.commands.manager

import pw.aru.psi.commands.ICategory
import pw.aru.psi.commands.ICommand

class CommandRegistry {
    val commands = LinkedHashMap<String, ICommand>()
    val categories = LinkedHashMap<String, ICategory>()

    val commandLookup = LinkedHashMap<ICommand, MutableList<String>>()
    val categoryNameLookup = LinkedHashMap<ICategory, String>()
    val categoryCommandsLookup = LinkedHashMap<ICategory, MutableList<ICommand>>()

    operator fun get(key: String) = commands[key]

    operator fun set(vararg names: String, command: ICommand) {
        registerCommand(names.toList(), command)
    }

    fun registerCommand(names: List<String>, command: ICommand) {
        val keys = names.asSequence()
            .map(String::toLowerCase)
            .distinct()
            .onEach { commands[it] = command }

        commandLookup.getOrPut(command, ::ArrayList).addAll(keys)
        command.category?.let { categoryCommandsLookup.getOrPut(it, ::ArrayList).add(command) }
    }

    fun registerCategory(value: String, category: ICategory) {
        categories[value] = category
        categoryNameLookup[category] = value
    }
}
