package pw.aru.psi.permissions

abstract class Permissions {
    class Just(val perm: Permission) : Permissions() {
        override fun check(permission: Collection<Permission>) = permission.contains(perm)

        override fun toString() = "**${perm.description}**"
    }

    class AnyOf(permissions: Collection<Permission>) : Permissions() {
        constructor(vararg permission: Permission) : this(permission.toSet())

        val permSet = permissions.toSet()

        override fun check(permission: Collection<Permission>): Boolean {
            return permission.any(permSet::contains)
        }

        override fun toString(): String {
            return when (permSet.size) {
                0 -> "anything (empty)"
                1 -> "**${permSet.first().description}**"
                2 -> "**${permSet.first().description}** or **${permSet.last().description}**"
                else -> {
                    val list = permSet.mapTo(ArrayList()) { "**${it.description}**" }
                    val last = list.removeAt(list.lastIndex)
                    list.joinToString(", ", postfix = " or $last")
                }
            }
        }
    }

    class AllOf(permissions: Collection<Permission>) : Permissions() {
        constructor(vararg permission: Permission) : this(permission.toSet())

        val permSet = permissions.toSet()

        override fun check(permission: Collection<Permission>): Boolean {
            return permission.containsAll(permSet)
        }

        override fun toString(): String {
            return when (permSet.size) {
                0 -> "anything (empty)"
                1 -> "**${permSet.first().description}**"
                2 -> "**${permSet.first().description}** and **${permSet.last().description}**"
                else -> {
                    val list = permSet.mapTo(ArrayList()) { "**${it.description}**" }
                    val last = list.removeAt(list.lastIndex)
                    list.joinToString(", ", postfix = " and $last")
                }
            }
        }
    }

    class AnyOfMulti(permissions: Collection<Permissions>) : Permissions() {
        constructor(vararg permission: Permissions) : this(permission.toSet())

        val permSet = permissions.toSet()

        override fun check(permission: Collection<Permission>): Boolean {
            return permSet.any { it.check(permission) }
        }

        override fun toString(): String {
            return when (permSet.size) {
                0 -> "anything (empty)"
                1 -> permSet.first().toString()
                2 -> "(${permSet.first()}) or (${permSet.last()})"
                else -> {
                    val list = permSet.mapTo(ArrayList(), Permissions::toString)
                    val last = list.removeAt(list.lastIndex)
                    list.joinToString(prefix = "(", separator = "), (", postfix = ") or ($last)")
                }
            }
        }
    }


    class AllOfMulti(permissions: Collection<Permissions>) : Permissions() {
        constructor(vararg permission: Permissions) : this(permission.toSet())

        val permSet = permissions.toSet()

        override fun check(permission: Collection<Permission>): Boolean {
            return permSet.all { it.check(permission) }
        }

        override fun toString(): String {
            return when (permSet.size) {
                0 -> "anything (empty)"
                1 -> permSet.first().toString()
                2 -> "(${permSet.first()}) and (${permSet.last()})"
                else -> {
                    val list = permSet.mapTo(ArrayList(), Permissions::toString)
                    val last = list.removeAt(list.lastIndex)
                    list.joinToString(prefix = "(", separator = "), (", postfix = ") and ($last)")
                }
            }
        }
    }

    object None : Permissions() {
        override fun check(permission: Collection<Permission>) = true

        override fun toString() = "anything"
    }

    abstract fun check(permission: Collection<Permission>): Boolean
}