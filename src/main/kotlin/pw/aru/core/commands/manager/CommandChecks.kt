package pw.aru.core.commands.manager

import com.mewna.catnip.entity.message.Message
import pw.aru.core.commands.ICommand
import pw.aru.core.permissions.Permission

interface CommandChecks {
    fun runChecks(message: Message, command: ICommand, userPerms: Set<Permission>): Boolean
}