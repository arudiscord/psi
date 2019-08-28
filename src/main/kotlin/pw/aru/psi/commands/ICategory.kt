package pw.aru.psi.commands

import com.mewna.catnip.entity.message.Embed
import com.mewna.catnip.entity.message.Message
import pw.aru.psi.BotDef
import pw.aru.psi.permissions.Permissions

/**
 * An [ICommand]'s category.
 */
interface ICategory {
    /**
     * The name of the category.
     */
    val categoryName: String

    val nsfw: Boolean

    interface Permission : ICategory {
        val permissions: Permissions
    }

    interface HelpDialog : ICategory {
        fun onHelp(def: BotDef, message: Message): Embed
    }

    interface HelpDialogProvider : ICategory {
        val helpHandler: HelpDialog
    }
}