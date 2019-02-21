package net.polarizedions.jamesbot.modules.internet;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.apis.SteamAPI;
import net.polarizedions.jamesbot.commands.ICommand;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.utils.CommandMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;
import static net.polarizedions.jamesbot.utils.IRCColors.BOLD;
import static net.polarizedions.jamesbot.utils.IRCColors.GREEN;
import static net.polarizedions.jamesbot.utils.IRCColors.LIGHT_GREY;
import static net.polarizedions.jamesbot.utils.IRCColors.ORANGE;
import static net.polarizedions.jamesbot.utils.IRCColors.RESET;

public class Steam extends Module implements ICommand {
    private static final Pattern URL_PATTERN = Pattern.compile(".*store.steampowered.com/app/([0-9-_]+).*$");

    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("steam")
                        .then(
                                argument("id", integer()).executes(c -> this.steam(c.getSource(), getInteger(c, "id")))
                        )
                        .then(
                                argument("url", greedyString()).executes(c -> this.parse(c.getSource(), getString(c, "url")))
                        )
        );
    }

    private int steam(CommandMessage source, int id) {
        SteamAPI.SteamApp app = SteamAPI.getApp(id);

        if (app == null) {
            source.respond("Sorry, I can't find that app.");
            return ReturnConstants.FAIL_REPLIED;
        }


        // Thanks to superaxander on discord <3
        String platformStr;
        List<String> availablePlatforms = new ArrayList<>();
        if (app.availableWindows) {
            availablePlatforms.add("Windows");
        }
        if (app.availableMac) {
            availablePlatforms.add("Mac");
        }
        if (app.availableLinux) {
            availablePlatforms.add("Linux");
        }

        if (availablePlatforms.size() == 1) {
            platformStr = ORANGE + availablePlatforms.get(0) + " only :(" + RESET;
        }
        else if (availablePlatforms.size() == 2) {
            platformStr = availablePlatforms.get(0) + " and " + availablePlatforms.get(1);
        }
        else {
            platformStr = GREEN + "all platforms" + RESET;
        }


        String discountStr = app.discountPercent == 0 ? "no discount" : app.discountPercent + "% discount";

        source.respond(String.format("%s%s%s: %s%s%s (%s), available for %s http://store.steampowered.com/app/%d%s [%s%s%s]%s", BOLD, app.name, RESET, GREEN, app.finalPriceFormatted, RESET, discountStr, platformStr, app.appId, LIGHT_GREY, RESET, String.join(LIGHT_GREY + "] [" + RESET, app.genres), LIGHT_GREY, RESET));

        return ReturnConstants.SUCCESS;
    }

    private int parse(CommandMessage source, String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        if (!matcher.matches()) {
            return ReturnConstants.FAIL_SILENT;
        }

        return this.steam(source, Integer.parseInt(matcher.group(1)));
    }

    @Override
    public String getHelp() {
        return "Get Steam games info";
    }

    @Override
    public String getUsage() {
        return "steam <appid / url>";
    }

    @Override
    public String getModuleName() {
        return "steam";
    }
}
