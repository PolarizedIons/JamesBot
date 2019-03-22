package net.polarizedions.jamesbot.modules.chat;

import net.polarizedions.jamesbot.apis.QMarkAPI;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.responders.IResponder;
import org.pircbotx.hooks.events.MessageEvent;

public class Qmark extends Module implements IResponder {
    public Qmark(Bot bot) {
        super(bot);
    }

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

    @Override
    public String getModuleName() {
        return "qmark";
    }
}
