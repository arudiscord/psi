package pw.aru.psi.executor.service

import pw.aru.utils.extensions.lang.threadGroupBasedFactory
import java.lang.Thread.currentThread
import java.util.concurrent.*
import java.util.function.Supplier

class JavaThreadTaskExecutor(private val scheduler: ScheduledExecutorService) : TaskExecutorService {
    companion object {
        val default by lazy {
            JavaThreadTaskExecutor(
                ScheduledThreadPoolExecutor(
                    minOf(Runtime.getRuntime().availableProcessors(), 4),
                    threadGroupBasedFactory("JavaThreadTaskExecutor.default")
                )
            )
        }
    }
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