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
        this.responders.add(new ButtcoinPlusPlus());

        // Make sure this runs last
        this.responders.add(new QmarkAPI());
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
