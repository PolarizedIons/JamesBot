package net.polarizedions.jamesbot.core;

import net.polarizedions.jamesbot.config.BotConfig;
import net.polarizedions.jamesbot.config.ConfigurationLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;

import java.io.IOException;

public class Bot extends ListenerAdapter {
    public static Bot instance;
    public static Logger logger = LogManager.getLogger("Jamesbot Core");
    private PircBotX bot;
    private ConfigurationLoader configLoader;

    public Bot() {
        instance = this;

        try {
            configLoader = new ConfigurationLoader();
            configLoader.load();
        }
        catch (IOException ex) {
            logger.error("Could not load config file! {}", ex);
            System.exit(1);
        }

        this.bot = new PircBotX(configLoader.build());
    }

    public BotConfig getBotConfig() {
        return this.configLoader.getBotConfig();
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

    public static void main(String[] args) {
        new Bot().run();
    }
}
