package pw.aru.psi.bootstrap

import com.mewna.catnip.util.CatnipMeta
import mu.KLogging
import pw.aru.psi.BotDef
import pw.aru.psi.exported.psi_version
import pw.aru.psi.logging.DiscordLogger
import pw.aru.utils.Colors
import pw.aru.utils.extensions.lang.limit
import pw.aru.utils.extensions.lang.simpleName
import pw.aru.utils.extensions.lib.description
import pw.aru.utils.extensions.lib.field
import java.io.PrintWriter
import java.io.StringWriter
import java.time.OffsetDateTime

/**
 * [PsiBootstrap] webhook logger.
 */
class BootstrapLogger(private val def: BotDef) {
    private companion object : KLogging()

    private val log = def.consoleWebhook?.let { DiscordLogger(it) }

    init {
        log?.text("——————————")
    }

    fun started() {
        logger.info("Booting up...")
        log?.embed {
            author("${def.botName} - Booting up...")
            color(Colors.discordYellow)

            description(
                "${def.botName} v${def.version} (Psi v$psi_version) + Catnip ${CatnipMeta.VERSION}",
                "Hol' up, we're starting!"
            )

            timestamp(OffsetDateTime.now())
        }
    }

    fun successful(shardCount: Int, categoryCount: Int, commandCount: Int) {
        logger.info { "Successful boot! $categoryCount categories and $commandCount commands loaded." }
        log?.embed {
            author("${def.botName} - Successful boot")
            color(Colors.discordGreen)

            description(
                "$shardCount shards loaded.",
                "$categoryCount categories loaded,",
                "$commandCount commands loaded."
            )

            timestamp(OffsetDateTime.now())
        }
    }

    fun failed(e: Exception) {
        logger.info("Boot failed.", e)
        log?.embed {

            author("${def.botName} - Boot failed")
            color(Colors.discordRed)

            field(
                e.javaClass.name,
                e.message?.limit(1024) ?: "<No message>"
            )
            field("More Info:", "See file below.")

            timestamp(OffsetDateTime.now())
        }
        log?.message {
            val s = StringWriter().also { e.printStackTrace(PrintWriter(it, true)) }.toString()
            val fileName = e.simpleName() + "_stacktrace.txt"
            addFile(fileName, s.toByteArray())
        }
    }

}