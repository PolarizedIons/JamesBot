package net.polarizedions.jamesbot.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectAttemptFailedEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class EventListener extends ListenerAdapter {
    private static Logger logger = LogManager.getLogger("EventListener");

    @Override
    public void onConnect(ConnectEvent event) throws Exception {
        logger.info("Connected!");
    }

    @Override
    public void onConnectAttemptFailed(ConnectAttemptFailedEvent event) throws Exception {
        logger.error("Failed to connect! Remaining attempts: {}, Exception: {}", event.getRemainingAttempts(), event.getConnectExceptions());
    }


    @Override
    public void onGenericMessage(GenericMessageEvent event) {
        // TODO: more requirements
        if (event.getMessage().startsWith(Bot.instance.getBotConfig().commandPrefix)) {
            if (this.runCommand(event)) {
                return;
            }
        }

        this.reactToMessage(event);
    }

    public boolean runCommand(GenericMessageEvent event) {
        return Bot.instance.getCommandManager().dispatch(event);
    }

    private void reactToMessage(GenericMessageEvent event) {
        // TODO
    }
}
