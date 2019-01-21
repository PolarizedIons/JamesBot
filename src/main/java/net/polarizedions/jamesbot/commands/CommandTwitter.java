package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.apis.Twitter;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
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

public class CommandTwitter implements ICommand {
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
        Twitter.Tweet tweet = Bot.instance.getTwitterAPI().getTweet(tweetId);

        if (tweet == null) {
            source.respond("Sorry, I can't find that tweet!");
            return ReturnConstants.FAIL_SILENT;
        }

        String text = tweet.text;

//        $text =~ s/(#[a-z][a-z0-9_]*)/\x0312$1\x0F/ig; # Color hashtags
//        $text =~ s/^RT (@\w{1,15}): /(\x0314RT $1\x0F) /; # Color retweets
//        $text =~ s/[\r\n]+/ /ig; # Remove newlines
//
//        $core->{'output'}->parse("MESSAGE>${chan}>${target}: \x02Tweet\x02 (by \x0303\@${author}\x0F) ${text} [ https://twitter.com/${author}/status/${id} ]");
//    }

    text = text.replaceAll("(#[a-zA-Z][a-zA-Z0-9_]*)", LIGHT_BLUE + "$1" + RESET); // Colour hashtags
    text = text.replaceAll("^RT (@\\w{1,15})", GREY + "RT $1" + RESET); // Colour RTs

    text = text.replaceAll("(@[a-zA-Z0-9_]*)", BLUE + "$1" + RESET); // Colour mentions



        source.respond(BOLD + "Tweet: " + CYAN + tweet.userDisplayName + RESET + " (" + BOLD + GREEN + "@" + tweet.userUsername + RESET + ") " + text + " [ https://twitter.com/" + tweet.userUsername + "/status/" + tweet.id + " ]");
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
}
