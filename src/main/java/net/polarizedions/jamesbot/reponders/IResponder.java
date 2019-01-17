package net.polarizedions.jamesbot.reponders;

import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;

public interface IResponder {
    default void run(MessageEvent msg) {

    }

    default void run(ActionEvent msg) {

    }
}
