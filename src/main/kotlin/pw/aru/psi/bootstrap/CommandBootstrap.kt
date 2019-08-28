package pw.aru.psi.bootstrap

import io.github.classgraph.ScanResult
import mu.KLogging
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import pw.aru.libs.kodein.jit.jitInstance
import pw.aru.psi.commands.*
import pw.aru.psi.commands.manager.CommandRegistry
import pw.aru.psi.executor.Executable
import pw.aru.psi.executor.RunAtStartup
import pw.aru.psi.executor.RunEvery
import pw.aru.psi.executor.service.TaskExecutorService
import pw.aru.utils.extensions.lang.allOf

class CommandBootstrap(private val scanResult: ScanResult, private val kodein: Kodein) {
    companion object : KLogging()

    private val tasks: TaskExecutorService by kodein.instance()
    private val registry: CommandRegistry by kodein.instance()

    fun createCategories() {
        scanResult.getClassesImplementing("pw.aru.psi.commands.ICategory")
            .filter { it.hasAnnotation("pw.aru.psi.commands.Category") }
            .loadClasses(ICategory::class.java)
            .forEach {
                try {
                    val meta = it.getAnnotation(Category::class.java)
                    if (it.isEnum) {
                        for (category in it.enumConstants) {
                            registry.registerCategory(
                                "${meta.value}#${(category as Enum<*>).name.toLowerCase()}", category
                            )
                            processExecutable(category)
                        }
                    } else {
                        val category = kodein.jitInstance(it)
                        registry.registerCategory(meta.value, category)
                        processExecutable(category)
                    }
                } catch (e: Exception) {
                    logger.error(e) { "Error while registering $it" }
                }
            }
    }

    fun createCommands() {
        scanResult.getClassesImplementing("pw.aru.psi.commands.ICommand")
            .filter { it.hasAnnotation("pw.aru.psi.commands.Command") }
            .loadClasses(ICommand::class.java)
            .forEach {
                try {
                    // command metadata
                    val meta = it.getAnnotation(Command::class.java)

                    // injectable category
                    val category = it.getAnnotation(Category::class.java)?.value?.let(registry.categories::get)

                    val command = kodein.maybeInject(category).jitInstance(it)
                    registry.registerCommand(meta.value.toList(), command)
                    processExecutable(command)
                } catch (e: Exception) {
                    logger.error(e) { "Error while registering $it" }
                }
            }
    }

    fun createProviders() {
        scanResult.getClassesImplementing("pw.aru.psi.commands.ICommandProvider")
            .filter { it.hasAnnotation("pw.aru.psi.commands.CommandProvider") }
            .loadClasses(ICommandProvider::class.java)
            .forEach {
                try {
                    // injectable category
                    val category = it.getAnnotation(Category::class.java)?.value?.let(registry.categories::get)

                    val provider = kodein.maybeInject(category).jitInstance(it)
                    provider.provide(registry)
                    processExecutable(provider)
                } catch (e: Exception) {
                    logger.error(e) { "Error while registering commands through $it" }
                }
            }
    }

    fun createStandalones() {
        scanResult.getClassesImplementing("pw.aru.psi.executor.Executable")
            .filter {
                allOf(
                    arrayOf(
                        "pw.aru.psi.executor.RunAtStartup",
                        "pw.aru.psi.executor.RunEvery"
                    ).any(it::hasAnnotation),
                    arrayOf(
                        "pw.aru.psi.commands.ICategory",
                        "pw.aru.psi.commands.ICommand",
                        "pw.aru.psi.commands.ICommandProvider"
                    ).none(it::implementsInterface)
                )
            }
            .loadClasses(Executable::class.java)
            .forEach {
                try {
                    processExecutable(kodein.jitInstance(it))
                } catch (e: Exception) {
                    logger.error(e) { "Error while executing $it" }
                }
            }
    }

    private fun Kodein.maybeInject(category: ICategory?): Kodein {
        if (category != null) {
            return Kodein {
                extend(this@maybeInject)
                bind<ICategory>() with instance(category)
            }
        }
        return this
    }

    private fun processExecutable(it: Any) {
        if (it is Executable) {
            when {
                it.javaClass.isAnnotationPresent(RunEvery::class.java) -> {
                    val meta = it.javaClass.getAnnotation(RunEvery::class.java)
                    tasks.task(meta.amount, meta.unit, meta.initialDelay, it.simpleName + meta, it::run)
                }
                it.javaClass.isAnnotationPresent(RunAtStartup::class.java) -> {
                    tasks.queue("${it.simpleName}@RunAtStartup", it::run)
                }
                else -> {
                    logger.warn { "Error: $it is an Executable but lacks an annotation" }
                }
            }
        }
    }

    private val Any.simpleName get() = javaClass.simpleName
}
