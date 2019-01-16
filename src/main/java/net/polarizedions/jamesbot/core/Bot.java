package net.polarizedions.jamesbot.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.io.IOException;

public class Bot extends ListenerAdapter {
    public static Bot instance;
    public static Logger logger = LogManager.getLogger("Jamesbot Core");
    private PircBotX bot;

    public Bot() {
        instance = this;

        bot = new PircBotX(BotConfig.build());
    }

    public void run() {
        try {
            bot.startBot();
        }
        catch (IOException | IrcException e) {
            logger.error("Exception running bot: {}", e);
        }
    }

    public static void main(String[] args) throws Exception {
        new Bot().run();
    }
}
