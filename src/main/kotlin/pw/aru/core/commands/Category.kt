package pw.aru.core.commands

import pw.aru.core.commands.help.Help
import pw.aru.core.permissions.Permission

interface Category {
    val categoryName: String
    val help: Help?
    val nsfw: Boolean
    val permissions: List<Permission>
}