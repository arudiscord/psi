@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LangExt")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandler
import java.util.concurrent.CompletableFuture
import java.net.http.HttpRequest.newBuilder as requestBuilder

private typealias RequestBuilder = HttpRequest.Builder.() -> Unit
private typealias CompletableResponse<T> = CompletableFuture<HttpResponse<T>>

inline fun <T> HttpClient.send(bodyHandler: BodyHandler<T>, block: RequestBuilder): HttpResponse<T> {
    return send(requestBuilder().also(block).build(), bodyHandler)
}

inline fun <T> HttpClient.sendAsync(bodyHandler: BodyHandler<T>, block: RequestBuilder): CompletableResponse<T> {
    return sendAsync(requestBuilder().also(block).build(), bodyHandler)
}
