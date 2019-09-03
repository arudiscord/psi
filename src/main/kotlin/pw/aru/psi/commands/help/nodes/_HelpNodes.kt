package pw.aru.psi.commands.help.nodes

import pw.aru.psi.commands.help.HelpEmbed

fun HelpEmbed.description(vararg values: String) = addNode(Description(*values))

fun HelpEmbed.usage(vararg values: UsageNode) = addNode(Usage(*values))

fun HelpEmbed.note(vararg values: String) = addNode(Note(*values))

fun HelpEmbed.seeAlso(vararg values: UsageNode) = addNode(SeeAlso(*values))

fun HelpEmbed.example(vararg values: String, prefixed: Boolean = true) = addNode(Example(*values, prefixed = prefixed))

fun HelpEmbed.field(name: String, vararg values: String) = addNode(Field(name, *values))
