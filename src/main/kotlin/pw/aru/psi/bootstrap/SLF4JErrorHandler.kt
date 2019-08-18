package pw.aru.psi.bootstrap

import org.slf4j.LoggerFactory

class SLF4JErrorHandler : CatnipErrorHandler {
    val logger = LoggerFactory.getLogger(CatnipBootstrap::class.java)

    override fun onReady(): (Throwable) -> Unit = {
        logger.error("An error happened while setting up Catnip", it)
    }

    override fun onCommandProcessor(): (Throwable) -> Unit = {
        logger.error("An error happened while processing commands", it)
    }

    override fun onGuildSubscriptions(): (Throwable) -> Unit = {
        logger.error("An error happened while posting guild join/leaves", it)
    }
}