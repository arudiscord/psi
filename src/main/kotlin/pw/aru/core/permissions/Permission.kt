package pw.aru.core.permissions

annotation class P(val value: String)

interface Permission {
    val name: String
    val description: String
}
