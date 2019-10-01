package pw.aru.ktbot

import pw.aru.psi.commands.Category
import pw.aru.psi.commands.ICategory

@Category("kt")
enum class KtCategories(override val categoryName: String, override val nsfw: Boolean = false) : ICategory {
    INFO("Information"),
    DEBUG("Debug Commands")
}