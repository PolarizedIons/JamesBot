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
        this.responders.add(new ResponderWhatIsLove());
        this.responders.add(new ResponderDice());
        this.responders.add(new ButtcoinCollector());
    }

    public boolean  dispatch(MessageEvent msg) {
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
