package net.polarizedions.jamesbot.reponders;

import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.hooks.events.ActionEvent;

public class ResponderHug implements IResponder {
    @Override
    public void run(ActionEvent msg) {
        if (msg.getAction().trim().equalsIgnoreCase("hugs " + Bot.instance.getPircBot().getNick())) {
            Bot.instance.getPircBot().sendIRC().action(msg.getChannelSource(), "<3");
        }
    }
}
