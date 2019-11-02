package pw.aru.psi.logging

import com.mewna.catnip.Catnip
import com.mewna.catnip.CatnipOptions
import com.mewna.catnip.entity.builder.EmbedBuilder
import com.mewna.catnip.entity.message.MessageOptions
import mu.KLogging

open class DiscordLogger(url: String) {
    companion object : KLogging() {
        private val client = Catnip.catnip(CatnipOptions("").validateToken(false))
        private var warningSent = false
    }

    init {
        if (!warningSent) {
            warningSent = true

            logger.warn("Discord Logging can't be enabled due to race conditions in the library.")
        }
    }

    //val webhook = client.parseWebhook(url).blockingGet()

    fun embed(builder: EmbedBuilder.() -> Unit) {
        //webhook.executeWebhook(EmbedBuilder().also(builder).build()).blockingGet()
    }

    fun text(vararg value: String) {
        //webhook.executeWebhook(value.joinToString("\n")).blockingGet()
    }

    fun message(builder: MessageOptions.() -> Unit) {
        //webhook.executeWebhook(MessageOptions().also(builder)).blockingGet()
    }
}