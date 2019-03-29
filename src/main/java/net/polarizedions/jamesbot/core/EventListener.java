package net.polarizedions.jamesbot.core;

import net.polarizedions.jamesbot.utils.CommandMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.ConnectAttemptFailedEvent;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;

public class EventListener extends ListenerAdapter {
    private static Logger logger = LogManager.getLogger("EventListener");
    private Bot bot;

    public EventListener(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void onConnect(ConnectEvent event) throws Exception {
        logger.info("Connected!");
        this.bot.getMainNick();
    }

    @Override
    public void onConnectAttemptFailed(ConnectAttemptFailedEvent event) throws Exception {
        logger.error("Failed to connect! Remaining attempts: {}, Exception: {}", event.getRemainingAttempts(), event.getConnectExceptions());
    }

    @Override
    public void onDisconnect(DisconnectEvent event) throws Exception {
        if (! this.bot.requestedQuit) {
            this.bot.stop();
            new Thread(() -> new Bot().run()).start();
        }
    }

    @Override
    public void onPrivateMessage(PrivateMessageEvent event) {
        this.runCommand(new CommandMessage(this.bot, event));
    }

    @Override
    public void onMessage(MessageEvent event) {
        String prefix = this.bot.getBotConfig().commandPrefix;
        String nick = this.bot.getPircBot().getNick();

        // Command Prefix
        if (event.getMessage().startsWith(prefix)) {
            String msg = event.getMessage().substring(prefix.length());
            if (this.runCommand(new CommandMessage(this.bot, event, msg))) {
                return;
            }
        }

        // Directed at us
        if (event.getMessage().toLowerCase().startsWith(nick.toLowerCase())) {
            String msg = event.getMessage().substring(nick.length()).trim();

            if (msg.startsWith(":") || msg.startsWith(",")) {
                msg = msg.substring(1).trim();
            }

            if (this.runCommand(new CommandMessage(this.bot, event, msg))) {
                return;
            }
        }

        if (this.reactToMessage(event)) {
            return;
        }

        this.bot.getMessageMemory(event.getChannel().getName()).add(event);
    }

    public boolean runCommand(CommandMessage msg) {
        try {
            return this.bot.getCommandManager().dispatch(msg);
        }
        catch (Exception e) {
            logger.error("Error dispatching command: {}", e);
            return false;
        }
    }

    @Override
    public void onAction(ActionEvent event) throws Exception {
        this.reactToMessage(event);
    }

    private boolean reactToMessage(MessageEvent event) {
        try {
            return this.bot.getResponderManager().dispatch(event);
        }
        catch (Exception e) {
            logger.error("Error reacting to command: {}", e);
            return false;
        }
    }

    private boolean reactToMessage(ActionEvent event) {
        try {
            return this.bot.getResponderManager().dispatch(event);
        }
        catch (Exception e) {
            logger.error("Error reacting to command: {}", e);
            return false;
        }
    }
}
