package pw.aru.psi.commands

import pw.aru.psi.commands.help.HelpProvider
import pw.aru.psi.permissions.Permissions

/**
 * An [ICommand]'s category.
 */
interface ICategory {
    /**
     * The name of the category.
     */
    val categoryName: String

    val permissions: Permissions
        get() = Permissions.none

    val nsfw: Boolean
        get() = false

    val help: HelpProvider?
        get() = this as? HelpProvider
}