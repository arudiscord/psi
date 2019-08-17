package pw.aru.psi.logging

import com.grack.nanojson.JsonWriter
import com.mewna.catnip.entity.builder.EmbedBuilder
import com.mewna.catnip.entity.impl.EntityBuilder
import pw.aru.utils.extensions.lang.sendAsync
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpResponse.BodyHandlers.discarding
import java.util.concurrent.CompletableFuture
import java.net.http.HttpRequest.BodyPublishers.ofString as stringBody

open class DiscordLogger(val url: String) {
    companion object {
        private val client = HttpClient.newHttpClient()
        private val dummyBuilder = EntityBuilder(null)
    }

    var last: CompletableFuture<*> = CompletableFuture.completedFuture<Void>(null)

    fun embed(builder: EmbedBuilder.() -> Unit) = apply {
        last = last.thenCompose {
            client.sendAsync(discarding()) {
                uri(URI.create(url))
                header("Content-Type", "application/json")
                POST(
                    // @formatter:off
                    stringBody(
                        JsonWriter.string()
                            .`object`()
                                .array("embeds")
                                    .value(dummyBuilder.embedToJson(EmbedBuilder().also(builder).build()))
                                .end()
                            .end()
                        .done()
                    )
                    // @formatter:on
                )
            }
        }
    }

    fun text(vararg value: String) = apply {
        last = last.thenCompose {
            client.sendAsync(discarding()) {
                uri(URI.create(url))
                header("Content-Type", "application/json")
                POST(
                    // @formatter:off
                    stringBody(
                        JsonWriter.string()
                            .`object`()
                                .value("content", value.joinToString("\n"))
                            .end()
                        .done()
                    )
                    // @formatter:on
                )
            }
        }
    }
}