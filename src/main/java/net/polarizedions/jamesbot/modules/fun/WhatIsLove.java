package net.polarizedions.jamesbot.modules.fun;

import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.responders.IResponder;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.regex.Pattern;

public class WhatIsLove extends Module implements IResponder {
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

    @Override
    public String getModuleName() {
        return "haddaway";
    }
}
