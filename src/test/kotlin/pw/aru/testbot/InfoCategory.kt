package pw.aru.testbot

import pw.aru.psi.commands.Category
import pw.aru.psi.commands.ICategory

@Category("info")
class InfoCategory : ICategory {
    override val categoryName = "Information"
    override val nsfw = false
}