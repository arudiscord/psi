package pw.aru.psi.commands.manager

import pw.aru.psi.commands.ICategory
import pw.aru.psi.commands.ICommand

class CommandRegistryImpl : CommandRegistry {
    // direct maps
    val commands = LinkedHashMap<String, ICommand>()
    val categories = LinkedHashMap<String, ICategory>()

    // lookup maps
    val commandNameLookup = LinkedHashMap<ICommand, LinkedHashSet<String>>()
    val categoryNameLookup = LinkedHashMap<ICategory, String>()
    val categoryCommandsLookup = LinkedHashMap<ICategory, LinkedHashSet<ICommand>>()

    override fun category(name: String): ICategory? {
        return categories[name]
    }

    override fun command(name: String): ICommand? {
        return commands[name]
    }

    override fun categories(): Set<ICategory> {
        return categoryNameLookup.keys.toSet()
    }

    override fun commands(): Set<ICommand> {
        return commandNameLookup.keys.toSet()
    }

    override fun namedCategories(): Set<Pair<ICategory, String>> {
        return categoryNameLookup.entries.asSequence()
            .map { (k, v) -> k to v }
            .toSet()
    }

    override fun namedCommands(): Set<Pair<ICommand, Set<String>>> {
        return commandNameLookup.entries.asSequence()
            .map { (k, v) -> k to v.toSet() }
            .toSet()
    }

    override fun categoryNames(): Set<String> {
        return categories.keys.toSet()
    }

    override fun commandNames(): Set<String> {
        return commands.keys.toSet()
    }

    override fun categoryCount(): Int {
        return categories.size
    }

    override fun commandCount(): Int {
        return commands.size
    }

    override fun names(command: ICommand): Set<String>? {
        return commandNameLookup[command]
    }

    override fun name(category: ICategory): String? {
        return categoryNameLookup[category]
    }

    override fun commands(category: ICategory): Set<ICommand>? {
        return categoryCommandsLookup[category]
    }

    override fun categorizedCommands(): Set<Pair<ICategory, Set<ICommand>>> {
        return categoryCommandsLookup.entries
            .map { (k, v) -> k to v.toSet() }
            .toSet()
    }

    override fun registerCommand(names: List<String>, command: ICommand) {
        commandNameLookup.getOrPut(command, ::LinkedHashSet).addAll(
            names.asSequence()
                .map(String::toLowerCase)
                .distinct()
                .onEach { commands[it] = command }
        )
        command.category?.let { categoryCommandsLookup.getOrPut(it, ::LinkedHashSet).add(command) }
    }

    override fun registerCategory(name: String, category: ICategory) {
        categories[name] = category
        categoryNameLookup[category] = name
    }
}