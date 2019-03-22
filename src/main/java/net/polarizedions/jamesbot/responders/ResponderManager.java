package net.polarizedions.jamesbot.responders;

import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;

public class ResponderManager {
    private List<IResponder> responders;

    public ResponderManager(Bot bot) {
        this.responders = new ArrayList<>();

        for (Module module : bot.getModuleManager().getModules(IResponder.class)) {
            if (module.isActive()) {
                responders.add((IResponder)module);
            }
        }
    }

    public boolean dispatch(MessageEvent msg) {
        for (IResponder responder : this.responders) {
            if (responder.run(msg)) {
                return true;
            }
        }

        return false;
    }

    public boolean dispatch(ActionEvent msg) {
        for (IResponder responder : this.responders) {
            if (responder.run(msg)) {
                return true;
            }
        }

        return false;
    }
}
