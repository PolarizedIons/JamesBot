package net.polarizedions.jamesbot.responders;

import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;

public interface IResponder {
    default boolean run(MessageEvent msg) {
        return false;
    }

    default boolean run(ActionEvent msg) {
        return false;
    }
}
