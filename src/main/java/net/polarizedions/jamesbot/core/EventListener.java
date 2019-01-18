package net.polarizedions.jamesbot.core;

import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ConnectAttemptFailedEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.MessageEvent;

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
    public void onMessage(MessageEvent event) {
        String prefix = Bot.instance.getBotConfig().commandPrefix;
        String nick = Bot.instance.getPircBot().getNick();

        // Command Prefix
        if (event.getMessage().startsWith(prefix)) {
            String msg = event.getMessage().substring(prefix.length());
            if (this.runCommand(msg, event)) {
                return;
            }
        }

        // Directed at us
        if (event.getMessage().toLowerCase().startsWith(nick.toLowerCase())) {
            String msg = event.getMessage().substring(nick.length()).trim();

            if (msg.startsWith(":") || msg.startsWith(",")) {
                msg = msg.substring(1).trim();
            }

            if (this.runCommand(msg, event)) {
                return;
            }
        }

        if (this.reactToMessage(event)) {
            return;
        }

        Bot.instance.getMessageMemory(event.getChannelSource()).add(event);
    }

    @Override
    public void onAction(ActionEvent event) throws Exception {
        this.reactToMessage(event);
    }

    public boolean runCommand(String message, MessageEvent event) {
        return Bot.instance.getCommandManager().dispatch(message, event);
    }

    private boolean reactToMessage(MessageEvent event) {
        return Bot.instance.getResponderManager().dispatch(event);
    }

    private boolean reactToMessage(ActionEvent event) {
        return Bot.instance.getResponderManager().dispatch(event);
    }
}
