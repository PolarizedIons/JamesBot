package net.polarizedions.jamesbot.responders;

import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.regex.Pattern;

public class ResponderWhatIsLove implements IResponder {
    @Override
    public boolean run(MessageEvent msg) {
        String nick = Bot.instance.getPircBot().getNick();
        Pattern pattern = Pattern.compile("^" + nick + "[:,]?\\s?What is love\\??", Pattern.CASE_INSENSITIVE);

        if (pattern.matcher(msg.getMessage()).matches()) {
            Bot.respond(msg, "Baby don't hurt me. Don't hurt me. No more.");
            return true;
        }

        return false;
    }
}
