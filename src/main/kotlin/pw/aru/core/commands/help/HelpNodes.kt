package pw.aru.core.commands.help

import pw.aru.core.permissions.Permissions
import java.awt.Color

sealed class BaseDescription

data class CommandDescription(
    val names: List<String>,
    val title: String,
    val permissions: Permissions? = null,
    val color: Color? = null,
    val thumbnail: String = "https://i.imgur.com/uxHqhwt.png"
) : BaseDescription()

data class CategoryDescription(
    val title: String,
    val permissions: Permissions? = null,
    val color: Color? = null,
    val thumbnail: String = "https://i.imgur.com/uxHqhwt.png"
) : BaseDescription()

//=== w ===//

sealed class HelpNode

data class Description(val value: String) : HelpNode() {
    constructor(vararg values: String) : this(values.joinToString("\n"))
}

data class Usage(val nodes: List<UsageNode>) : HelpNode() {
    constructor(vararg nodes: UsageNode) : this(nodes.toList())

    fun value(prefix: String) = nodes.joinToString("\n") { it.value(prefix) }
}

data class Note(val value: String) : HelpNode() {
    constructor(vararg values: String) : this(values.joinToString("\n"))
}

data class SeeAlso(val value: String) : HelpNode() {
    constructor(vararg values: UsageNode) : this(values.joinToString("\n"))

    companion object {
        @JvmStatic
        @JvmName("simpleList")
        operator fun get(vararg values: String) =
            SeeAlso(values.joinToString("` `", "`", "`"))

        fun ofList(names: List<String>) = SeeAlso(names.joinToString("` `", "`", "`"))
    }
}

data class Example(val values: List<String>, val withPrefix: Boolean = true) : HelpNode() {
    constructor(vararg values: String, withPrefix: Boolean = true) : this(values.toList(), withPrefix)

    fun value(prefix: String): String {
        return (if (withPrefix) values.map { "$prefix$it" } else values)
            .joinToString(prefix = "```\n", separator = "\n", postfix = "\n```")
    }
}

data class Field(val name: String, val value: String) : HelpNode() {
    constructor(name: String, vararg values: String) : this(name, values.joinToString("\n"))
}

//=== w ===//

sealed class UsageNode {
    abstract fun value(prefix: String): String
}

data class CommandUsage(val command: String, val extra: String?, val description: String) : UsageNode() {
    constructor(command: String, description: String) : this(command, null, description)

    override fun value(prefix: String) = if (extra != null) {
        "`$prefix$command` $extra - $description"
    } else {
        "`$prefix$command` - $description"
    }
}

data class TextUsage(val value: String) : UsageNode() {
    override fun value(prefix: String) = value
}

object UsageSeparator : UsageNode() {
    override fun value(prefix: String) = ""
}