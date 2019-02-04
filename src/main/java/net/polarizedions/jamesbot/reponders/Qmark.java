package net.polarizedions.jamesbot.reponders;

import net.polarizedions.jamesbot.apis.QMarkAPI;
import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.hooks.events.MessageEvent;

public class Qmark implements IResponder {
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
        else if (!msg.getChannel().getName().equalsIgnoreCase(myNick)) {
            return false;
        }

        String response = QMarkAPI.ask(message);
        if (!response.isEmpty()) {
            msg.respond(response);
        }
        return true;
    }
}
