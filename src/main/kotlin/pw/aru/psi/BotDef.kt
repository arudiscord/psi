package pw.aru.psi

import com.mewna.catnip.CatnipOptions
import org.kodein.di.Kodein
import pw.aru.utils.Colors
import java.awt.Color

/**
 * A Bot Definitions file.
 *
 * Use [PsiApplication] with a instance of this interface to start a bot.
 */
interface BotDef {
    /**
     * Bot name. Can be anything.
     *
     * Used internally in webhook messages.
     *
     * **Example**: `"MyBot"`
     */
    val botName: String

    /**
     * Bot version. Can be anything.
     *
     * Used internally in webhook messages.
     *
     * **Example**: `"1.0"`
     */
    val version: String

    /**
     * Base package of your commands and executables.
     *
     * Used to narrow down the classes to scan for.
     *
     * **Example**: `"com.coderwebsite.mybot"`
     */
    val basePackage: String

    /**
     * Prefixes that your bot answers to.
     *
     * **Example**: `listOf("!", "-")`
     */
    val prefixes: List<String>

    /**
     * Splashes to be randomly shown as the bot's presence.
     *
     * Can be a empty list.
     *
     * **Example**: `listOf("New commands!", "Plays music!")`
     */
    val splashes: List<String> get() = emptyList()

    /**
     * The main command of the bot, meant to be shown at the splash.
     *
     * Can be null.
     *
     * **Example**: `"about"`
     */
    val mainCommandName: String? get() = "help"

    /**
     * Main color of the bot.
     *
     * Used to generate the [pw.aru.psi.commands.help.HelpEmbed] dialogs.
     *
     * **Example**: [Colors.blurple][pw.aru.utils.Colors.blurple]
     */
    val mainColor: Color get() = Colors.blurple

    /**
     * Options used to create the [com.mewna.catnip.Catnip] instance.
     *
     * You're supposed to at least create a [CatnipOptions] interface with your bot token.
     *
     * **Example**: `new CatnipOptions("YOUR_TOKEN_HERE")`
     */
    val catnipOptions: CatnipOptions

    /**
     * Custom [Kodein] module used for dependency injection.
     *
     * Can be `null`.
     *
     * This is your entry point to override
     * [the default command processor][pw.aru.psi.commands.manager.CommandProcessor],
     * [add a custom error handling][pw.aru.psi.bootstrap.ErrorHandler],
     * [override the task executor][pw.aru.psi.executor.service.TaskExecutorService],
     * as well as adding extra objects which can be injected on commands.
     */
    val kodeinModule: Kodein.Module? get() = null
}