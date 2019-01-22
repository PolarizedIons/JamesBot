package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.apis.Steam;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.utils.CommandMessage;

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

public class CommandSteam implements ICommand {
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
        Steam.SteamApp app = Steam.getApp(id);

        if (app == null) {
            source.respond("Sorry, I can't find that app.");
            return ReturnConstants.FAIL_SILENT;
        }

        String platformStr = GREEN + "all platforms!" + RESET;
        if (app.avaliableWindows && !app.avaliableMac && !app.avaliableLinux) {
            platformStr = ORANGE + "Windows only :(" + RESET;
        }
        else if (! app.avaliableWindows && app.avaliableMac && ! app.avaliableLinux) {
            platformStr = ORANGE + "Mac only :(" + RESET;
        }
        else if (! app.avaliableWindows && ! app.avaliableMac && app.avaliableLinux) {
            platformStr = ORANGE + "Linux only :(" + RESET;
        }
        else if (app.avaliableWindows && app.avaliableMac && !app.avaliableLinux) {
            platformStr = "Windows and Mac";
        } else if (app.avaliableWindows && !app.avaliableMac && app.avaliableLinux) {
            platformStr = "Windows and Linux";
        } else if (!app.avaliableWindows && app.avaliableMac && app.avaliableLinux) {
            platformStr = "Mac and Linux";
        }


//        $core->{'output'}->parse("MESSAGE>${chan}>${target}: \x02${title}\x02 \x0303\$${price}\x0F ${discount}, available for ${platforms} http://store.steampowered.com/app/${app} ${genres}");

        String discountStr = app.discountPercent == 0 ? "no discount" : app.discountPercent + "% discount";

        source.respond(BOLD + app.name + RESET + ": " + GREEN + app.finalPriceFormatted + RESET + " (" + discountStr + "), available for " + platformStr + " http://store.steampowered.com/app/" + app.appId +  LIGHT_GREY + " [" + RESET + String.join(LIGHT_GREY + "] [" + RESET , app.genres) + LIGHT_GREY + "]" + RESET);

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
}
