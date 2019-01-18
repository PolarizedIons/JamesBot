package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.polarizedions.jamesbot.apis.Youtube;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.hooks.events.MessageEvent;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class CommandYoutube implements ICommand {

    @Override
    public void register(CommandDispatcher<MessageEvent> dispatcher) {
        dispatcher.register(
            literal("youtube").then(
                    argument("link", string()).executes(c -> this.youtube(c.getSource(), getString(c, "link")))
            )
        );
    }

    private int youtube(MessageEvent source, String link) {
        System.out.println("running " + link);
        Youtube api = Bot.instance.getYoutubeAPI();
        if (api == null) {
            source.respond("Youtube API is unavailable :(");
            return ReturnConstants.FAIL_SILENT;
        }

        Youtube.YoutubeVideo video = api.getVideo(link);

        if (video == null) {
            source.respond("Can't get that video, sorry :(");
            return ReturnConstants.FAIL_SILENT;
        }

        source.respond("Title: " + video.title + " by:" + video.channel);

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
