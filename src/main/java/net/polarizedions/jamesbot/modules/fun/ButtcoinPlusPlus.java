package net.polarizedions.jamesbot.modules.fun;

import net.polarizedions.jamesbot.apis.ButtcoinAPI;
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
        if (!message.startsWith(this.bot.getBotConfig().commandPrefix)) {
            return false;
        }
        message = message.substring(this.bot.getBotConfig().commandPrefix.length());

        Matcher matcher = PLUS_PLUS_PATTERN.matcher(message);

        if (!matcher.matches()) {
            return false;
        }

        String from = msg.getUser().getNick();
        String to = matcher.group(1);

        if (from.equalsIgnoreCase(to)) {
            this.bot.getPircBot().sendIRC().notice(from, "You are " + to + "!");
            return true;
        }

        ButtcoinAPI buttApi = this.bot.getButtcoinAPI();
        if (!buttApi.isAccountActive(from)) {
            buttApi.activateAccount(from);
        }

        if (!buttApi.isAccountActive(to)) {
            this.bot.getPircBot().sendIRC().notice(from, "Sorry, but " + to + " does not have an active account");
        }

        int currBalance = buttApi.getAccount(from).balance;
        if (currBalance < 1) {
            Bot.noticePM(msg, "Sorry, you (" + currBalance + ") do not have enough funds to transfer 1 buttcoin.");
            return true;
        }

        if (buttApi.transfer(from, to, 1) != null) {
            Bot.noticePM(msg, "Sorry, I couldn't do that. Do you have enough buttcoins?");
            return true;
        }


        ButtcoinAccount fromAccount = buttApi.getAccount(from);
        ButtcoinAccount toAccount = buttApi.getAccount(to);

        this.bot.getPircBot().sendIRC().notice(from, String.format("You (%d) have sent %d buttcoin to %s (%d) with the message: Plus Plus.", fromAccount.balance, 1, to, toAccount.balance));
        this.bot.getPircBot().sendIRC().notice(to, String.format("You (%d) have received %d buttcoin from %s (%d) [Plus Plus]", toAccount.balance, 1, from, fromAccount.balance));

        return true;
    }

    @Override
    public String getModuleName() {
        return "buttcoin";
    }
}
