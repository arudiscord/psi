package pw.aru.javabot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pw.aru.psi.commands.Category;
import pw.aru.psi.commands.ICategory;
import pw.aru.psi.commands.help.HelpProvider;

@Category("j")
public enum JavaCategories implements ICategory {
    INFO("Information"),
    DEBUG("Debug Commands");

    private final String categoryName;

    JavaCategories(String categoryName) {
        this.categoryName = categoryName;
    }

    @NotNull
    @Override
    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public boolean getNsfw() {
        return false;
    }

    @Nullable
    @Override
    public HelpProvider getHelp() {
        return null;
    }
}