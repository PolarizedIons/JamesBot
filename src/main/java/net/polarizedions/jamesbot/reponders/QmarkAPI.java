package net.polarizedions.jamesbot.reponders;

import net.polarizedions.jamesbot.apis.QMarkAPI;
import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.regex.Pattern;

public class QmarkAPI implements IResponder {
    @Override
    public boolean run(MessageEvent msg) {
        String myNick = Bot.instance.getPircBot().getNick();
        String message = msg.getMessage();

        if (message.startsWith(myNick)) {
            message = message.substring(myNick.length());

            if (message.startsWith(":") || message.startsWith(",")) {
                message = message.substring(1).trim();
            }
        }
        else if (! msg.getChannel().getName().equalsIgnoreCase(myNick)) {
            return false;
        }


        msg.respond(QMarkAPI.ask(message));
        return true;
    }
}
