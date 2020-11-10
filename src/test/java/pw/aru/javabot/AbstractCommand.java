package pw.aru.javabot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pw.aru.psi.commands.ICategory;
import pw.aru.psi.commands.ICommand;
import pw.aru.psi.commands.context.CommandContext;
import pw.aru.psi.commands.help.HelpProvider;

public abstract class AbstractCommand implements ICommand {
    protected ICategory category;
    protected boolean nsfw;

    protected AbstractCommand() {
    }

    @Nullable
    @Override
    public ICategory getCategory() {
        return category;
    }

    @Override
    public boolean getNsfw() {
        return nsfw;
    }

    @Nullable
    @Override
    public HelpProvider getHelp() {
        return null;
    }

    @Override
    public abstract void call(@NotNull CommandContext ctx);
}
