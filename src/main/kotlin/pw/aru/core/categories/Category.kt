package pw.aru.core.categories

import pw.aru.core.commands.help.Help
import pw.aru.core.permissions.Permission

abstract class Category(
    val categoryName: String,
    val help: Help? = null,
    val nsfw: Boolean = false,
    val permissions: List<Permission> = emptyList()
) {
    override fun toString() = "Category(name = $categoryName, permissions = $permissions)"
}