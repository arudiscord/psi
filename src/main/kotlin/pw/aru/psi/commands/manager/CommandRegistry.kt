package pw.aru.psi.commands.manager

import pw.aru.psi.commands.ICategory
import pw.aru.psi.commands.ICommand

interface CommandRegistry {
    fun category(name: String): ICategory?
    fun categories(): Set<ICategory>
    fun namedCategories(): Set<Pair<ICategory, String>>
    fun categoryNames(): Set<String>
    fun categoryCount(): Int
    fun registerCategory(name: String, category: ICategory)
    fun name(category: ICategory): String?
    fun commands(category: ICategory): Set<ICommand>?

    fun command(name: String): ICommand?
    fun commands(): Set<ICommand>
    fun namedCommands(): Set<Pair<ICommand, Set<String>>>
    fun categorizedCommands(): Set<Pair<ICategory, Set<ICommand>>>
    fun commandNames(): Set<String>
    fun commandCount(): Int
    fun names(command: ICommand): Set<String>?
    fun registerCommand(names: List<String>, command: ICommand)
}