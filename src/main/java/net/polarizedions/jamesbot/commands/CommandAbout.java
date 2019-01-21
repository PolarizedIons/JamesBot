package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.BuildInfo;
import net.polarizedions.jamesbot.utils.CommandMessage;

import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class CommandAbout implements ICommand {
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

    private int about(CommandMessage source) {
        source.respond("I am Jamesbot. Built by PolarizedIons in loving memory of Janebot by Gambit. < " + WEBSITE_URL + " >");
        return ReturnConstants.SUCCESS;
    }

    private int version(CommandMessage source) {
        source.respond("I am Jamesbot v" + BuildInfo.version + ", built " + BuildInfo.buildtime + " running on java " + System.getProperty("java.version") + ". < " + REPO_URL + " >");
        return ReturnConstants.SUCCESS;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }
}
