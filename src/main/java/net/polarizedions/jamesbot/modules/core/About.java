package net.polarizedions.jamesbot.modules.core;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.commands.ICommand;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.BuildInfo;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.utils.CommandMessage;
import org.jetbrains.annotations.NotNull;

import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class About extends Module implements ICommand {
    private static final String WEBSITE_URL = "https://polarizedions.net/";
    private static final String REPO_URL = "https://github.com/PolarizedIons/JamesBot";

    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("about").executes(c -> this.about(c.getSource()))
        );

        dispatcher.register(
                literal("version").executes(c -> this.version(c.getSource()))
        );
    }

    private int about(@NotNull CommandMessage source) {
        source.respond(String.format("I am Jamesbot. Built by PolarizedIons, in loving memory of Janebot by Gambit. < %s >", WEBSITE_URL));
        return ReturnConstants.SUCCESS;
    }

    private int version(@NotNull CommandMessage source) {
        source.respond(String.format("I am Jamesbot v%s, built %s running on java %s. < %s >", BuildInfo.version, BuildInfo.buildtime, System.getProperty("java.version"), REPO_URL));
        return ReturnConstants.SUCCESS;
    }

    @Override
    public String getHelp() {
        return "Shows information about the bot";
    }

    @Override
    public String getUsage() {
        return "about, version";
    }

    @Override
    public String getModuleName() {
        return "about";
    }
}
