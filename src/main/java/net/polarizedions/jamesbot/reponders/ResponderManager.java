package net.polarizedions.jamesbot.reponders;

import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;

public class ResponderManager {
    private List<IResponder> responders;

    public ResponderManager() {
        this.responders = new ArrayList<>();

        this.responders.add(new ResponderActions());
    }

    public void dispatch(MessageEvent msg) {
        for (IResponder responder : this.responders) {
            responder.run(msg);
        }
    }

    public void dispatch(ActionEvent msg) {
        for (IResponder responder : this.responders) {
            responder.run(msg);
        }
    }
}
