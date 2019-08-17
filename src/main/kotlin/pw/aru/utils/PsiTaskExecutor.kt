package pw.aru.utils

import pw.aru.psi.executor.TaskExecutorService
import pw.aru.utils.extensions.lang.threadGroupBasedFactory
import java.lang.Thread.currentThread
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.function.Supplier

object PsiTaskExecutor : TaskExecutorService {
    private val scheduler = ScheduledThreadPoolExecutor(
        minOf(Runtime.getRuntime().availableProcessors(), 4),
        threadGroupBasedFactory("PsiTaskExecutor")
    )

    override fun task(
        period: Long,
        unit: TimeUnit,
        initialDelay: Long,
        name: String?,
        block: () -> Unit
    ): ScheduledFuture<*> {
        return scheduler.scheduleAtFixedRate(task(block, name), initialDelay, period, unit)
    }

    override fun queue(
        name: String?,
        block: () -> Unit
    ): CompletableFuture<*> {
        return CompletableFuture.runAsync(task(block, name).asRunnable(), scheduler)
    }

    override fun <T> compute(
        name: String?,
        block: () -> T
    ): CompletableFuture<T> {
        return CompletableFuture.supplyAsync(task(block, name).asSupplier(), scheduler)
    }

    override fun schedule(
        delay: Long,
        unit: TimeUnit,
        name: String?,
        block: () -> Unit
    ): ScheduledFuture<*> {
        return scheduler.schedule(task(block, name), delay, unit)
    }

    private fun <T> task(task: () -> T, name: String?): () -> T {
        return name?.let {
            {
                val t = currentThread()
                val n = t.name
                t.name = name

                try {
                    task()
                } finally {
                    t.name = n
                }
            }
        } ?: task
    }

    private fun (() -> Unit).asRunnable(): Runnable = Runnable(this::invoke)

    private fun <R> (() -> R).asSupplier(): Supplier<R> = Supplier(this::invoke)
}
