package pw.aru.psi.commands.help.nodes

import java.awt.Color

sealed class BaseDescription {
    abstract val title: String
    abstract val color: Color?
    abstract val thumbnail: String?
}

data class CommandDescription(
    val names: List<String>,
    override val title: String,
    override val color: Color? = null,
    override val thumbnail: String? = null
) : BaseDescription()

data class CategoryDescription(
    override val title: String,
    override val color: Color? = null,
    override val thumbnail: String? = null
) : BaseDescription()
