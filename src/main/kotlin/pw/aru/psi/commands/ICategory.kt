package pw.aru.psi.commands

import pw.aru.psi.commands.help.HelpProvider

/**
 * An [ICommand]'s category.
 */
interface ICategory {
    /**
     * The name of the category.
     */
    val categoryName: String

    val nsfw: Boolean
        get() = false

    val help: HelpProvider?
        get() = this as? HelpProvider
}