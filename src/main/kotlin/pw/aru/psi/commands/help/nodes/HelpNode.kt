package pw.aru.psi.commands.help.nodes

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
    constructor(vararg values: String, prefixed: Boolean = true) : this(values.toList(), prefixed)

    fun value(prefix: String): String {
        return (if (withPrefix) values.map { "$prefix$it" } else values)
            .joinToString(prefix = "```\n", separator = "\n", postfix = "\n```")
    }
}

data class Field(val name: String, val value: String) : HelpNode() {
    constructor(name: String, vararg values: String) : this(name, values.joinToString("\n"))
}
