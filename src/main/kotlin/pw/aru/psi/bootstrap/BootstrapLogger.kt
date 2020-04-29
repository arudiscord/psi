package pw.aru.psi.bootstrap

import mu.KLogging
import pw.aru.psi.BotDef

/**
 * [PsiBootstrap] webhook logger.
 */
class BootstrapLogger(private val def: BotDef) {
    private companion object : KLogging()

    fun started() {
        logger.info("Booting up...")
    }

    fun successful(shardCount: Int, categoryCount: Int, commandCount: Int) {
        logger.info { "Successful boot! $shardCount shards, $categoryCount categories and $commandCount commands loaded." }
    }

    fun failed(e: Exception) {
        logger.info("Boot failed.", e)
    }
}