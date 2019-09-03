package pw.aru.psi.permissions

import java.util.function.Predicate

abstract class Permissions : Predicate<Collection<Permission>> {
    companion object {
        val none: Permissions
            get() = Empty

        fun of(vararg permissions: Permission): Permissions {
            return when (permissions.size) {
                0 -> Empty
                1 -> Single(permissions.single())
                else -> All(permissions.toSet())
            }
        }

        fun anyOf(vararg permissions: Permission): Permissions {
            return when (permissions.size) {
                0 -> Empty
                1 -> Single(permissions.single())
                else -> Any(permissions.toSet())
            }
        }

        fun of(vararg permissions: Permissions): Permissions {
            if (permissions.all { it == Empty }) {
                return Empty
            }

            val allPerms = HashSet<Permission>()
            val blocks = HashSet<Permissions>()

            for (permission in permissions) {
                when (permission) {
                    Empty -> Unit
                    is Single -> allPerms.add(permission.perm)
                    is All -> allPerms.addAll(permission.perms)
                    is AllMulti -> for (perm in permission.perms) {
                        when (perm) {
                            Empty -> Unit
                            is Single -> allPerms.add(perm.perm)
                            is All -> allPerms.addAll(perm.perms)
                            else -> blocks.add(permission)
                        }
                    }
                    else -> blocks.add(permission)
                }
            }

            return when {
                allPerms.isEmpty() && blocks.isEmpty() -> Empty
                allPerms.isNotEmpty() && blocks.isEmpty() -> of(*allPerms.toTypedArray())
                allPerms.isEmpty() && blocks.isNotEmpty() -> AllMulti(blocks)
                else -> AllMulti(
                    listOfNotNull(
                        allPerms.takeIf(Set<*>::isNotEmpty)?.let { listOf(All(it)) },
                        blocks.takeIf(Set<*>::isNotEmpty)
                    ).flatten().toSet()
                )
            }
        }

        fun anyOf(vararg permissions: Permissions): Permissions {
            if (permissions.any { it == Empty }) {
                return Empty
            }

            val anyPerms = HashSet<Permission>()
            val blocks = HashSet<Permissions>()

            for (permission in permissions) {
                when (permission) {
                    Empty -> return Empty
                    is Single -> anyPerms.add(permission.perm)
                    is Any -> anyPerms.addAll(permission.perms)
                    is AnyMulti -> for (perm in permission.perms) {
                        when (perm) {
                            Empty -> return Empty
                            is Single -> anyPerms.add(perm.perm)
                            is All -> anyPerms.addAll(perm.perms)
                            else -> blocks.add(permission)
                        }
                    }
                    else -> blocks.add(permission)
                }
            }

            return when {
                anyPerms.isEmpty() && blocks.isEmpty() -> Empty
                anyPerms.isNotEmpty() && blocks.isEmpty() -> anyOf(*anyPerms.toTypedArray())
                anyPerms.isEmpty() && blocks.isNotEmpty() -> AnyMulti(blocks)
                else -> AnyMulti(
                    listOfNotNull(
                        anyPerms.takeIf(Set<*>::isNotEmpty)?.let { listOf(Any(it)) },
                        blocks.takeIf(Set<*>::isNotEmpty)
                    ).flatten().toSet()
                )
            }
        }
    }

    internal object Empty : Permissions() {
        override fun test(t: Collection<Permission>) = true

        override fun toString() = "anything"
    }

    internal class Single(val perm: Permission) : Permissions() {
        override fun test(permission: Collection<Permission>) = perm in permission

        override fun toString() = "**${perm.description}**"
    }

    internal class All(val perms: Set<Permission>) : Permissions() {
        override fun test(permission: Collection<Permission>): Boolean {
            return permission.containsAll(perms)
        }

        override fun toString(): String {
            return when (perms.size) {
                0 -> "anything (empty)"
                1 -> "**${perms.first().description}**"
                2 -> "**${perms.first().description}** and **${perms.last().description}**"
                else -> {
                    val list = perms.mapTo(ArrayList()) { "**${it.description}**" }
                    val last = list.removeAt(list.lastIndex)
                    list.joinToString(", ", postfix = " and $last")
                }
            }
        }
    }

    internal class Any(val perms: Set<Permission>) : Permissions() {
        override fun test(permission: Collection<Permission>): Boolean {
            return permission.any(perms::contains)
        }

        override fun toString(): String {
            return when (perms.size) {
                0 -> "anything (empty)"
                1 -> "**${perms.first().description}**"
                2 -> "**${perms.first().description}** or **${perms.last().description}**"
                else -> {
                    val list = perms.mapTo(ArrayList()) { "**${it.description}**" }
                    val last = list.removeAt(list.lastIndex)
                    list.joinToString(", ", postfix = " or $last")
                }
            }
        }
    }

    internal class AllMulti(val perms: Set<Permissions>) : Permissions() {
        override fun test(permission: Collection<Permission>): Boolean {
            return perms.all { it.test(permission) }
        }

        override fun toString(): String {
            return when (perms.size) {
                0 -> "anything (empty)"
                1 -> perms.first().toString()
                2 -> "(${perms.first()}) and (${perms.last()})"
                else -> {
                    val list = perms.mapTo(ArrayList(), Permissions::toString)
                    val last = list.removeAt(list.lastIndex)
                    list.joinToString(prefix = "(", separator = "), (", postfix = ") and ($last)")
                }
            }
        }
    }

    internal class AnyMulti(val perms: Set<Permissions>) : Permissions() {
        override fun test(permission: Collection<Permission>): Boolean {
            return perms.any { it.test(permission) }
        }

        override fun toString(): String {
            return when (perms.size) {
                0 -> "anything (empty)"
                1 -> perms.first().toString()
                2 -> "(${perms.first()}) or (${perms.last()})"
                else -> {
                    val list = perms.mapTo(ArrayList(), Permissions::toString)
                    val last = list.removeAt(list.lastIndex)
                    list.joinToString(prefix = "(", separator = "), (", postfix = ") or ($last)")
                }
            }
        }
    }
}