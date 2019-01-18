package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.apis.Youtube;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.hooks.events.MessageEvent;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;
import static net.polarizedions.jamesbot.utils.IRCColors.BOLD;
import static net.polarizedions.jamesbot.utils.IRCColors.CYAN;
import static net.polarizedions.jamesbot.utils.IRCColors.GREEN;
import static net.polarizedions.jamesbot.utils.IRCColors.GREY;
import static net.polarizedions.jamesbot.utils.IRCColors.MAGENTA;
import static net.polarizedions.jamesbot.utils.IRCColors.ORANGE;
import static net.polarizedions.jamesbot.utils.IRCColors.RED;
import static net.polarizedions.jamesbot.utils.IRCColors.RESET;

public class CommandYoutube implements ICommand {

    @Override
    public void register(CommandDispatcher<MessageEvent> dispatcher) {
        dispatcher.register(
            literal("youtube").then(
                    argument("link", greedyString()).executes(c -> this.youtube(c.getSource(), getString(c, "link")))
            )
        );
    }

    private int youtube(MessageEvent source, String link) {
        link = link.split("\\s")[0];

        System.out.println("running " + link);
        Youtube api = Bot.instance.getYoutubeAPI();
        if (api == null) {
            source.respond("Youtube API is unavailable :(");
            return ReturnConstants.FAIL_SILENT;
        }Youtube.YoutubeVideo video = null;
        try {
        video = api.getVideo(link);
    }catch (Exception e) {
        e.printStackTrace();
    }
        if (video == null) {
            source.respond("Can't get that video, sorry :(");
            return ReturnConstants.FAIL_SILENT;
        }
//\x02\"${title}\"\x02 \x0306[${hours}:${minutes}:${seconds}]\x0F (by \x0303${author}\x0F) \x0314${views}\x0F views, \x0303${likes}\x0F likes, \x0304${dislikes}\x0F dislikes https://youtu.be/${video} ${restrictions}"
//{BOLD}{TITLE}{BOLD} {MAGENTA}[{HOURS}:{MINUTES}:{SECONDS}]{RESET} (by {GREEN}{CHANNEL}{RESET}) {GREY}{VIEWS}{RESET} views, {GREEN}{LIKES}{RESET} likes, {RED}{DISLIKES}{RESET} dislikes https://youtu.be/{ID} {RESTRICTED_MSG}

// \x0314no region restrictions\x0F  {GREY}no region restrictions{RESET}
// \x0307unavailable in some regions\x0F)   {ORANGE}unabaliable in some regopms{RESET}
//        source.respond("Title: " + video.title + " by:" + video.channel);

        String restrictionStr = GREY + "no region restrictions" + RESET;
        if (video.isRegionRestricted) {
            restrictionStr = ORANGE + "unavailable in some regions" + RESET;
        }

        source.respondWith(BOLD + video.title + MAGENTA + " [" + video.duration + "]" + RESET + " (by " + GREEN + video.channel + RESET + ") " + CYAN + video.viewCount + RESET + " views, " + GREEN + video.likeCount + RESET + " likes, " + RED + video.dislikeCount + RESET + " dislikes. https://youtu.be/" + video.id + "(" + restrictionStr + ")");

        return ReturnConstants.SUCCESS;
    }

    @Override
    public String getHelp() {
        return "Get youtube info";
    }

    @Override
    public String getUsage() {
        return "youtube <link/id>";
    }
}
