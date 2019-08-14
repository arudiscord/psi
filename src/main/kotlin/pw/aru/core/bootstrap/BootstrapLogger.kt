package pw.aru.core.bootstrap

import com.mewna.catnip.util.CatnipMeta
import mu.KLogging
import pw.aru.core.BotDef
import pw.aru.core.exported.aruCore_version
import pw.aru.core.logging.DiscordLogger
import pw.aru.utils.Colors
import pw.aru.utils.extensions.lang.limit
import pw.aru.utils.extensions.lib.description
import pw.aru.utils.extensions.lib.field
import java.time.OffsetDateTime

class BootstrapLogger(val def: BotDef) : DiscordLogger(def.consoleWebhook) {
    private companion object : KLogging()

    init {
        text("——————————")
    }

    fun started() {
        logger.info("Booting up...")
        embed {
            author("${def.botName} - Booting up...")
            color(Colors.discordYellow)

            description(
                "${def.botName} v${def.version} (aruCore v$aruCore_version) + Catnip ${CatnipMeta.VERSION}",
                "Hol' up, we're starting!"
            )

            timestamp(OffsetDateTime.now())
        }
    }

    fun successful(shardCount: Int, commandCount: Int) {
        logger.info { "Successful boot! $commandCount commands loaded." }
        embed {
            author("${def.botName} - Successful boot")
            color(Colors.discordGreen)

            description(
                "$shardCount shards loaded.",
                "$commandCount commands loaded."
            )

            timestamp(OffsetDateTime.now())
        }
    }

    fun failed(e: Exception) {
        logger.info("Boot failed.", e)
        embed {
            author("${def.botName} - Boot failed")
            color(Colors.discordRed)

            field(
                e.javaClass.name,
                e.message!!.limit(1024)
            )
            field("More Info:", def.bootstrap.handleError(e))

            timestamp(OffsetDateTime.now())
        }
    }

}