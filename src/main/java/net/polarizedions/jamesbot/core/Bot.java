package net.polarizedions.jamesbot.core;

import net.polarizedions.jamesbot.apis.Buttcoin;
import net.polarizedions.jamesbot.apis.Youtube;
import net.polarizedions.jamesbot.commands.CommandManager;
import net.polarizedions.jamesbot.config.BotConfig;
import net.polarizedions.jamesbot.config.ConfigurationLoader;
import net.polarizedions.jamesbot.config.StaffEntry;
import net.polarizedions.jamesbot.database.Database;
import net.polarizedions.jamesbot.reponders.ResponderManager;
import net.polarizedions.jamesbot.utils.CommandMessage;
import net.polarizedions.jamesbot.utils.FixedSizeQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.types.GenericChannelUserEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class Bot {
    public static Bot instance;
    public static Logger logger = LogManager.getLogger("Jamesbot Core");

    private PircBotX bot;
    private CommandManager commandManager;
    private ResponderManager responderManager;
    private ConfigurationLoader configLoader;
    private Database database;

    private Youtube youtubeAPI;
    private Buttcoin buttcoinAPI;

    private HashMap<String, FixedSizeQueue<MessageEvent>> messageMemory;

    public Bot() {
        instance = this;

        try {
            this.configLoader = new ConfigurationLoader();
            this.configLoader.load();
        } catch (IOException ex) {
            logger.error("Could not load config file! {}", ex);
            System.exit(1);
        }

        this.commandManager = new CommandManager();
        this.responderManager = new ResponderManager();

        this.messageMemory = new HashMap<>();
        this.database = new Database(this.getBotConfig().databaseConfig);

        if (!this.getBotConfig().apiKeys.youtube.isEmpty()) {
            this.youtubeAPI = new Youtube(this.getBotConfig().apiKeys.youtube);
        }
        this.buttcoinAPI = new Buttcoin();

        this.bot = new PircBotX(configLoader.build());
    }


    public BotConfig getBotConfig() {
        return this.configLoader.getBotConfig();
    }

    public static void notice(GenericChannelUserEvent msg, String content) {
        Bot.noticeWith(msg, msg.getUser().getNick() + ": " + content);
    }

    public static void noticeWith(@NotNull GenericChannelUserEvent msg, String content) {
        msg.getBot().sendIRC().notice(msg.getChannel().getName(), content);
    }

    public static void noticePM(@NotNull GenericChannelUserEvent msg, String content) {
        msg.getBot().sendIRC().notice(msg.getUser().getNick(), content);
    }

    public static void noticePM(String to, String content) {
        Bot.instance.getPircBot().sendIRC().notice(to, content);
    }

    public static void action(GenericChannelUserEvent msg, String content) {
        msg.getBot().sendIRC().action(msg.getChannel().getName(), content);
    }

    public static boolean staffCommandRequirement(CommandMessage commandMessage) {
        List<StaffEntry> staff = Bot.instance.getBotConfig().staff;
        for (StaffEntry staffMember : staff) {
            if (staffMember.matches(commandMessage.getUser())) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        new Bot().run();
    }

    public void run() {
        try {
            this.bot.startBot();
        } catch (IOException | IrcException e) {
            logger.error("Exception running bot: {}", e);
        }
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ResponderManager getResponderManager() {
        return responderManager;
    }

    public Youtube getYoutubeAPI() {
        return youtubeAPI;
    }

    public Buttcoin getButtcoinAPI() {
        return buttcoinAPI;
    }

    public Database getDatabase() {
        return database;
    }

    public FixedSizeQueue<MessageEvent> getMessageMemory(String channel) {
        if (!this.messageMemory.containsKey(channel)) {
            this.messageMemory.put(channel, new FixedSizeQueue<>(this.getBotConfig().memorySize));
        }

        return messageMemory.get(channel);
    }

    public void saveBotConfig() {
        try {
            this.configLoader.save();
        } catch (IOException ex) {
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

    public PircBotX getPircBot() {
        return this.bot;
    }
}
