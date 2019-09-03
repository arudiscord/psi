@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("LangExt")
@file:JvmMultifileClass

package pw.aru.utils.extensions.lang

import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Future
import kotlin.reflect.KProperty

@JvmName("futureValue")
fun <V> Future<V>.value(): V = get()

@JvmName("completionValue")
fun <V> CompletionStage<V>.value(): V = stageToFuture().join()

@JvmName("futureCompletionValue")
fun <V, T> T.value(): V where T : Future<V>, T : CompletionStage<V> = get()

@JvmName("futureGetValue")
operator fun <V> Future<V>.getValue(r: Any?, p: KProperty<*>): V = value()

@JvmName("completionGetValue")
operator fun <V> CompletionStage<V>.getValue(r: Any?, p: KProperty<*>): V = value()

@JvmName("futureCompletionGetValue")
operator fun <V, T> T.getValue(r: Any?, p: KProperty<*>): V where T : Future<V>, T : CompletionStage<V> = value()

private fun <T> CompletionStage<T>.stageToFuture(): CompletableFuture<T> {
    return if (this is CompletableFuture<*>) {
        this as CompletableFuture<T>
    } else {
        toCompletableFuture()
    }
}

fun <T> Iterable<CompletionStage<T>>.awaitAll(): CompletionStage<Void> {
    return CompletableFuture.allOf(*map { it.stageToFuture() }.toTypedArray())
}

fun <T> Array<CompletionStage<T>>.awaitAll(): CompletionStage<Void> {
    return CompletableFuture.allOf(*map { it.stageToFuture() }.toTypedArray())
}
