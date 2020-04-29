package pw.aru.psi.commands.context

import com.mewna.catnip.Catnip
import com.mewna.catnip.entity.channel.DMChannel
import com.mewna.catnip.entity.guild.Member
import com.mewna.catnip.entity.user.User
import io.reactivex.rxjava3.core.Single
import java.util.*

data class ContextMember(val user: User, val member: Member) : User by user, Member by member {
    override fun catnip(): Catnip = user.catnip()
    override fun idAsLong() = user.idAsLong()
    override fun asMention() = user.asMention()
    override fun createDM(): Single<DMChannel> = user.createDM()

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is ContextMember -> idAsLong() == other.idAsLong()
            is User -> idAsLong() == other.idAsLong()
            is Member -> idAsLong() == other.idAsLong()
            else -> false
        }
    }

    override fun hashCode(): Int {
        return Objects.hashCode(idAsLong())
    }
}