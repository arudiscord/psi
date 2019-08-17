package pw.aru.psi.commands

import pw.aru.psi.commands.help.Help
import pw.aru.psi.permissions.Permission

interface Category {
    val categoryName: String
    val help: Help?
    val nsfw: Boolean
    val permissions: List<Permission>
}