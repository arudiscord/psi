package pw.aru.psi.commands

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Command(
    vararg val value: String
)

