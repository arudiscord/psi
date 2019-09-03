package pw.aru.psi.commands.help.nodes

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