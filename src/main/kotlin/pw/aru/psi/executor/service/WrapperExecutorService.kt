package pw.aru.psi.executor.service

import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.TimeUnit

class WrapperExecutorService(private val service: TaskExecutorService) : AbstractExecutorService() {
    override fun execute(command: Runnable) {
        service.queue(null, command::run)
    }

    override fun shutdown() = throw UnsupportedOperationException()

    override fun shutdownNow(): MutableList<Runnable> = throw UnsupportedOperationException()

    override fun isShutdown(): Boolean = false

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean = false

    override fun isTerminated(): Boolean = false
}