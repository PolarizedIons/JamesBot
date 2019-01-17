package net.polarizedions.jamesbot.core;

import net.polarizedions.jamesbot.commands.CommandManager;
import net.polarizedions.jamesbot.config.BotConfig;
import net.polarizedions.jamesbot.config.ConfigurationLoader;
import net.polarizedions.jamesbot.reponders.ResponderManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;

public class Bot extends ListenerAdapter {
    public static Bot instance;
    public static Logger logger = LogManager.getLogger("Jamesbot Core");

    private PircBotX bot;
    private CommandManager commandManager;
    private ResponderManager responderManager;
    private ConfigurationLoader configLoader;

    public Bot() {
        instance = this;

        try {
            this.configLoader = new ConfigurationLoader();
            this.configLoader.load();
        }
        catch (IOException ex) {
            logger.error("Could not load config file! {}", ex);
            System.exit(1);
        }

        this.commandManager = new CommandManager();
        this.responderManager = new ResponderManager();

        this.bot = new PircBotX(configLoader.build());
    }

    public BotConfig getBotConfig() {
        return this.configLoader.getBotConfig();
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ResponderManager getResponderManager() {
        return responderManager;
    }

    public PircBotX getPircBot() {
        return this.bot;
    }

    public void saveBotConfig() {
        try {
            this.configLoader.save();
        }
        catch (IOException ex) {
            logger.error("Could not save config file! {}", ex);
        }
    }

    public void run() {
        try {
            this.bot.startBot();
        }
        catch (IOException | IrcException e) {
            logger.error("Exception running bot: {}", e);
        }
    }

    public static void noticeReply(MessageEvent msg, String content) {
        Bot.notice(msg, msg.getUser().getNick() + ": " + content);
    }

    public static void notice(@NotNull MessageEvent msg, String content) {
        msg.getBot().sendIRC().notice(msg.getChannelSource(), content);
    }

    public static void main(String[] args) {
        new Bot().run();
    }
}
