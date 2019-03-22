package net.polarizedions.jamesbot.core;

import net.polarizedions.jamesbot.apis.ButtcoinAPI;
import net.polarizedions.jamesbot.apis.TwitterAPI;
import net.polarizedions.jamesbot.apis.YoutubeAPI;
import net.polarizedions.jamesbot.commands.CommandManager;
import net.polarizedions.jamesbot.config.BotConfig;
import net.polarizedions.jamesbot.config.ConfigurationLoader;
import net.polarizedions.jamesbot.config.StaffEntry;
import net.polarizedions.jamesbot.database.Database;
import net.polarizedions.jamesbot.modules.ModuleManager;
import net.polarizedions.jamesbot.responders.ResponderManager;
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
import java.util.Map;

public class Bot {
    public static Logger logger = LogManager.getLogger("Jamesbot Core");
    public boolean requestedQuit = false;

    private PircBotX bot;
    private CommandManager commandManager;
    private ResponderManager responderManager;
    private ConfigurationLoader configLoader;
    private Database database;
    private ModuleManager moduleManager;

    private YoutubeAPI youtubeAPI;
    private TwitterAPI twitterAPI;
    private ButtcoinAPI buttcoinAPI;

    private HashMap<String, FixedSizeQueue<MessageEvent>> messageMemory;

    public Bot() {
        logger.info("Starting Jamesbot v" + BuildInfo.version + " built: " + BuildInfo.buildtime);

        this.moduleManager = new ModuleManager(this);

        try {
            this.configLoader = new ConfigurationLoader(this);
            this.configLoader.load();
        }
        catch (IOException ex) {
            logger.error("Could not load config file! {}", ex);
            System.exit(1);
        }

        this.moduleManager.initConfig(this.getBotConfig());

        this.commandManager = new CommandManager(this);
        this.responderManager = new ResponderManager(this);

        this.messageMemory = new HashMap<>();
        this.database = new Database(this.getBotConfig().databaseConfig);

        if (!this.getBotConfig().apiKeys.youtube.isEmpty()) {
            this.youtubeAPI = new YoutubeAPI(this.getBotConfig().apiKeys.youtube);
        }
        this.twitterAPI = new TwitterAPI();
        if (this.moduleManager.isEnabled("twitter")) {
            this.twitterAPI.auth(this.getBotConfig());
        }
        this.buttcoinAPI = new ButtcoinAPI(this);

        logger.info("Loaded {} modules!", moduleManager.getModuleCount());
        for (Map.Entry<String, Boolean> entry : moduleManager.getState().entrySet()) {
            logger.debug("  - {}:\t{}", entry.getKey(), entry.getValue());
        }

        this.bot = new PircBotX(configLoader.build());
    }

    public static void respond(GenericChannelUserEvent msg, String content) {
        respondWith(msg, msg.getUser().getNick() + ": " + content);
    }

    public static void respondWith(@NotNull GenericChannelUserEvent msg, String content) {
        msg.getBot().sendIRC().message(msg.getChannel().getName(), content);
    }

    public static void respondPM(@NotNull GenericChannelUserEvent msg, String content) {
        msg.getBot().sendIRC().message(msg.getUser().getNick(), content);
    }

    public static void noticePM(@NotNull GenericChannelUserEvent msg, String content) {
        msg.getBot().sendIRC().notice(msg.getUser().getNick(), content);
    }

    public static void action(@NotNull GenericChannelUserEvent msg, String content) {
        msg.getBot().sendIRC().action(msg.getChannel().getName(), content);
    }

    public static boolean staffCommandRequirement(CommandMessage commandMessage) {
        List<StaffEntry> staff = commandMessage.getBot().getBotConfig().staff;
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

    public BotConfig getBotConfig() {
        return this.configLoader.getBotConfig();
    }

    public void run() {
        try {
            this.bot.startBot();
        }
        catch (IOException | IrcException e) {
            logger.error("Exception running bot: {}", e);
        }
    }

    public void stop() {
        this.requestedQuit = true;
        this.bot.close();
    }

    void getMainNick() {
        new Thread(() -> {
            try {
                Thread.sleep(30000);
            }
            catch (InterruptedException e) { /* NOOP */ }

            while (!this.getPircBot().getNick().equalsIgnoreCase(this.getBotConfig().nick)) {
                try {
                    Thread.sleep(30000);
                }
                catch (InterruptedException e) { /* NOOP */ }
                logger.debug("Trying to get main nick...");
                this.getPircBot().sendIRC().changeNick(this.getBotConfig().nick);
            }
            logger.debug("Got main nick!");
        }).start();
    }

    public PircBotX getPircBot() {
        return this.bot;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public ResponderManager getResponderManager() {
        return responderManager;
    }

    public YoutubeAPI getYoutubeAPI() {
        return youtubeAPI;
    }

    public ButtcoinAPI getButtcoinAPI() {
        return buttcoinAPI;
    }

    public TwitterAPI getTwitterAPI() {
        return twitterAPI;
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
}
