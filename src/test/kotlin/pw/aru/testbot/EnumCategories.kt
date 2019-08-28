package pw.aru.testbot

import pw.aru.psi.commands.Category
import pw.aru.psi.commands.ICategory

@Category("enum")
enum class EnumCategories(override val categoryName: String, override val nsfw: Boolean = false) : ICategory {
    HELPFUL("Helpful Commands"),
    DEBUG("Debug Commands")
}