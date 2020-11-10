package pw.aru.javabot;

import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kodein.di.Kodein;
import pw.aru.psi.BotDef;
import pw.aru.psi.PsiApplication;
import pw.aru.utils.Colors;

import java.awt.*;
import java.util.List;

public class JavaExampleBot implements BotDef {
    public static void main(String[] args) {
        new PsiApplication(new JavaExampleBot()).init();
    }

    @NotNull
    @Override
    public String getBotName() {
        return "JavaBot";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "1.0";
    }

    @NotNull
    @Override
    public String getBasePackage() {
        return "pw.aru.javabot";
    }

    @NotNull
    @Override
    public List<String> getPrefixes() {
        return List.of("!");
    }

    @NotNull
    @Override
    public List<String> getSplashes() {
        return List.of("Java!");
    }

    @Nullable
    @Override
    public String getMainCommandName() {
        return null;
    }

    @NotNull
    @Override
    public Color getMainColor() {
        return Colors.INSTANCE.getDiscordPurple();
    }

    @NotNull
    @Override
    public DefaultShardManagerBuilder getBuilder() {
        return DefaultShardManagerBuilder.createLight(System.getenv("token"), GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGES);
    }

    @Nullable
    @Override
    public Kodein.Module getKodeinModule() {
        return null;
    }
}