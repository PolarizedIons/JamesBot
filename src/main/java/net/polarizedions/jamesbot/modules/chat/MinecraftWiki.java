package net.polarizedions.jamesbot.modules.chat;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.apis.MediaWiki;
import net.polarizedions.jamesbot.commands.ICommand;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.utils.CommandMessage;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class MinecraftWiki extends Module implements ICommand {
    public MinecraftWiki(Bot bot) {
        super(bot);
    }

    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("mwiki")
                    .then(
                            argument("query", greedyString()).executes(c -> this.search(c.getSource(), getString(c, "query")))
                    )
        );
    }

    private int search(CommandMessage source, String query) {
        String url = MediaWiki.searchPage("https://minecraft.gamepedia.com", query);
        if (url == null) {
            source.respond("Sorry, I couldn't find that page :(");

            return ReturnConstants.FAIL_REPLIED;
        }

        source.respond(url);
        return ReturnConstants.SUCCESS;
    }


    @Override
    public String getHelp() {
        return "Finds a Minecraft Wiki page";
    }

    @Override
    public String getUsage() {
        return "mwiki <query>";
    }

    @Override
    public String getModuleName() {
        return "mwiki";
    }
}
