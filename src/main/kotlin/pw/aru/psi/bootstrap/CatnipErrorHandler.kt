package pw.aru.psi.bootstrap

interface CatnipErrorHandler {
    fun onReady(): (Throwable) -> Unit

    fun onCommandProcessor(): (Throwable) -> Unit

    fun onGuildSubscriptions(): (Throwable) -> Unit
}