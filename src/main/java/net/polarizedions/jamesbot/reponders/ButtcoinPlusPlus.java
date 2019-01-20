package net.polarizedions.jamesbot.reponders;

import net.polarizedions.jamesbot.apis.Buttcoin;
import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ButtcoinPlusPlus implements IResponder {
    private static final Pattern PLUS_PLUS_PATTERN = Pattern.compile("^([A-Za-z0-9\\\\`_^{}|.~-]+)\\+\\+$");

    @Override
    public boolean run(MessageEvent msg) {
        String message = msg.getMessage();
        if (! message.startsWith(Bot.instance.getBotConfig().commandPrefix)) {
            return false;
        }
        message = message.substring(Bot.instance.getBotConfig().commandPrefix.length());

        Matcher matcher = PLUS_PLUS_PATTERN.matcher(message);

        if (! matcher.matches()) {
            System.out.println("plus plus didn't match");
            return false;
        }
        System.out.println("plus plus matched");


        String from = msg.getUser().getNick();
        String to = matcher.group(1);
        System.out.println("from " + from + " to " + to);
        boolean result = Bot.instance.getButtcoinAPI().transfer(from, to, 1);
        if (! result) {
            Bot.noticePM(msg, "Sorry, I couldn't do that. Do you have enough buttcoins?");
        }
        else {
            Bot.noticePM(msg, "You gave " + to + " 1 buttcoin.");

            if (Bot.instance.getButtcoinAPI().isAccountActive(to)) {
                Bot.noticePM(to, from + " has given you 1 buttcoin.");
            }
        }

        return result;
    }
}
