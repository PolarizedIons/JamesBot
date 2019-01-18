package net.polarizedions.jamesbot.core;

import net.polarizedions.jamesbot.apis.Youtube;
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
import org.pircbotx.hooks.types.GenericChannelUserEvent;

import java.io.IOException;
import java.util.HashMap;

public class Bot extends ListenerAdapter {
    public static Bot instance;
    public static Logger logger = LogManager.getLogger("Jamesbot Core");

    private PircBotX bot;
    private CommandManager commandManager;
    private ResponderManager responderManager;
    private ConfigurationLoader configLoader;

    private Youtube youtubeAPI;

    private HashMap<String, FixedSizeQueue<MessageEvent>> messageMemory;

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

        this.messageMemory = new HashMap<>();

        if (! this.getBotConfig().apiKeys.youtube.isEmpty()) {
            this.youtubeAPI = new Youtube(this.getBotConfig().apiKeys.youtube);
        }

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

    public Youtube getYoutubeAPI() {
        return youtubeAPI;
    }

    public FixedSizeQueue<MessageEvent> getMessageMemory(String channel) {
        if (! this.messageMemory.containsKey(channel)) {
            this.messageMemory.put(channel, new FixedSizeQueue<>(this.getBotConfig().memorySize));
        }

        return messageMemory.get(channel);
    }

    public void saveBotConfig() {
        try {
            this.configLoader.save();
        }
        catch (IOException ex) {
            logger.error("Could not save config file! {}", ex);
        }
    }

    public void debug(Object... content) {
        String channel = this.getBotConfig().debugChannel;

        if (channel.isEmpty()) {
            return;
        }

        String[] strContent = new String[content.length];
        for (int i = 0; i < content.length; i++) {
            strContent[i] = String.valueOf(content[i]);
        }

        String msg = "[DEBUG]: " + String.join(" ", strContent);
        this.getPircBot().sendIRC().message(channel, msg);
    }

    public void run() {
        try {
            this.bot.startBot();
        }
        catch (IOException | IrcException e) {
            logger.error("Exception running bot: {}", e);
        }
    }

    public static void noticeReply(GenericChannelUserEvent msg, String content) {
        Bot.notice(msg, msg.getUser().getNick() + ": " + content);
    }

    public static void notice(@NotNull GenericChannelUserEvent msg, String content) {
        msg.getBot().sendIRC().notice(msg.getChannel().getName(), content);
    }

    public static void action(GenericChannelUserEvent msg, String content) {
        msg.getBot().sendIRC().action(msg.getChannel().getName(), content);
    }

    public static void main(String[] args) {
        new Bot().run();
    }
}
