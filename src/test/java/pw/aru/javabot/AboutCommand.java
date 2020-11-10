package pw.aru.javabot;

import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;
import pw.aru.psi.commands.Category;
import pw.aru.psi.commands.Command;
import pw.aru.psi.commands.ICategory;
import pw.aru.psi.commands.context.CommandContext;

import static pw.aru.psi.exported.PsiExported.psi_version;

@Command({"about", "thanks"})
@Category("j#info")
class AboutCommand extends AbstractCommand {
    public AboutCommand(ICategory c) {
        category = c;
    }

    @Override
    public void call(@NotNull CommandContext ctx) {
        ctx.getChannel().sendMessage(
            new EmbedBuilder()
                .appendDescription("This is an example Java bot made using psi " + psi_version + ".\nThanks for using it <3.")
                .build()
        ).submit();
    }
}
