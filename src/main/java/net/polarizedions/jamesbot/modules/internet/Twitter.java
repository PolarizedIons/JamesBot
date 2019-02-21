package net.polarizedions.jamesbot.modules.internet;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.apis.TwitterAPI;
import net.polarizedions.jamesbot.commands.ICommand;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.utils.CommandMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;
import static net.polarizedions.jamesbot.utils.IRCColors.BLUE;
import static net.polarizedions.jamesbot.utils.IRCColors.BOLD;
import static net.polarizedions.jamesbot.utils.IRCColors.CYAN;
import static net.polarizedions.jamesbot.utils.IRCColors.GREEN;
import static net.polarizedions.jamesbot.utils.IRCColors.GREY;
import static net.polarizedions.jamesbot.utils.IRCColors.LIGHT_BLUE;
import static net.polarizedions.jamesbot.utils.IRCColors.RESET;

public class Twitter extends Module implements ICommand {
    private static final Pattern TWEET_URL_PATTERN = Pattern.compile(".*twitter\\.com/[a-zA-Z0-9-_]+/status/([0-9]+).*$");

    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("tweet").then(
                        argument("url", greedyString()).executes(c -> this.tweet(c.getSource(), getString(c, "url")))
                )
        );
    }

    private int tweet(CommandMessage source, String url) {
        url = url.split("\\s")[0];

        Matcher matcher = TWEET_URL_PATTERN.matcher(url);
        if (!matcher.matches()) {
            return ReturnConstants.FAIL_SILENT;
        }

        long tweetId = Long.parseLong(matcher.group(1));
        TwitterAPI.Tweet tweet = Bot.instance.getTwitterAPI().getTweet(tweetId);

        if (tweet == null) {
            source.respond("Sorry, I can't find that tweet!");
            return ReturnConstants.FAIL_REPLIED;
        }

        String text = tweet.text;

        text = text.replaceAll("(#[a-zA-Z][a-zA-Z0-9_]*)", LIGHT_BLUE + "$1" + RESET); // Colour hashtags
        text = text.replaceAll("^RT (@\\w{1,15})", GREY + "RT $1" + RESET); // Colour RTs

        text = text.replaceAll("(@[a-zA-Z0-9_]*)", BLUE + "$1" + RESET); // Colour mentions


        source.respond(String.format("%sTweet: %s%s%s (%s%s@%s%s) %s [ https://twitter.com/%s/status/%d ]", BOLD, CYAN, tweet.userDisplayName, RESET, BOLD, GREEN, tweet.userUsername, RESET, text, tweet.userUsername, tweet.id));
        return ReturnConstants.SUCCESS;
    }

    @Override
    public String getHelp() {
        return "Get tweets and display them!";
    }

    @Override
    public String getUsage() {
        return "tweet (url)";
    }

    @Override
    public String getModuleName() {
        return "twitter";
    }
}
