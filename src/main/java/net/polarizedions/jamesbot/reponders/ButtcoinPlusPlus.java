package net.polarizedions.jamesbot.reponders;

import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.database.ButtcoinAccount;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ButtcoinPlusPlus implements IResponder {
    private static final Pattern PLUS_PLUS_PATTERN = Pattern.compile("^([A-Za-z0-9\\\\`_^{}|.~-]+)\\+\\+$");

    @Override
    public boolean run(MessageEvent msg) {
        String message = msg.getMessage();
        if (!message.startsWith(Bot.instance.getBotConfig().commandPrefix)) {
            return false;
        }
        message = message.substring(Bot.instance.getBotConfig().commandPrefix.length());

        Matcher matcher = PLUS_PLUS_PATTERN.matcher(message);

        if (!matcher.matches()) {
            return false;
        }

        String from = msg.getUser().getNick();
        String to = matcher.group(1);

        if (from.equalsIgnoreCase(to)) {
            Bot.noticePM(from, "You are " + to + "!");
            return true;
        }

        if (! Bot.instance.getButtcoinAPI().isAccountActive(to)) {
            Bot.noticePM(from, "Sorry, but " + to + " does not have an active account");
        }

        boolean result = Bot.instance.getButtcoinAPI().transfer(from, to, 1);
        if (!result) {
            Bot.noticePM(msg, "Sorry, I couldn't do that. Do you have enough buttcoins?");
        } else {

            ButtcoinAccount fromAccount = Bot.instance.getButtcoinAPI().getAccount(from);
            ButtcoinAccount toAccount = Bot.instance.getButtcoinAPI().getAccount(to);

            Bot.noticePM(from, String.format("You (%d) have sent %d buttcoin to %s (%d)", fromAccount.balance, 1, to, toAccount.balance));
            Bot.noticePM(to, String.format("You (%d) have received %d buttcoin from %s (%d) [Plus Plus]", toAccount.balance, 1, from, fromAccount.balance));
        }

        return result;
    }
}
