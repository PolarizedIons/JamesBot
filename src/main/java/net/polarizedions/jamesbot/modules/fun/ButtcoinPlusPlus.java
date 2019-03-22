package net.polarizedions.jamesbot.modules.fun;

import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.database.ButtcoinAccount;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.responders.IResponder;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ButtcoinPlusPlus extends Module implements IResponder {
    private static final Pattern PLUS_PLUS_PATTERN = Pattern.compile("^([A-Za-z0-9\\\\`_^{}|.~-]+)\\+\\+$");

    public ButtcoinPlusPlus(Bot bot) {
        super(bot);
    }

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


        if (!Bot.instance.getButtcoinAPI().isAccountActive(from)) {
            Bot.instance.getButtcoinAPI().activateAccount(from);
        }

        if (!Bot.instance.getButtcoinAPI().isAccountActive(to)) {
            Bot.noticePM(from, "Sorry, but " + to + " does not have an active account");
        }

        int currBalance = Bot.instance.getButtcoinAPI().getAccount(from).balance;
        if (currBalance < 1) {
            Bot.noticePM(msg, "Sorry, you (" + currBalance + ") do not have enough funds to transfer 1 buttcoin.");
            return true;
        }

        if (Bot.instance.getButtcoinAPI().transfer(from, to, 1) != null) {
            Bot.noticePM(msg, "Sorry, I couldn't do that. Do you have enough buttcoins?");
            return true;
        }


        ButtcoinAccount fromAccount = Bot.instance.getButtcoinAPI().getAccount(from);
        ButtcoinAccount toAccount = Bot.instance.getButtcoinAPI().getAccount(to);

        Bot.noticePM(from, String.format("You (%d) have sent %d buttcoin to %s (%d) with the message: Plus Plus.", fromAccount.balance, 1, to, toAccount.balance));
        Bot.noticePM(to, String.format("You (%d) have received %d buttcoin from %s (%d) [Plus Plus]", toAccount.balance, 1, from, fromAccount.balance));

        return true;
    }

    @Override
    public String getModuleName() {
        return "buttcoin";
    }
}
