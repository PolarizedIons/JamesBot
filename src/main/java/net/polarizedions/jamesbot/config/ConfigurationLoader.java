package net.polarizedions.jamesbot.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.core.BuildInfo;
import net.polarizedions.jamesbot.core.EventListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.Configuration;
import org.pircbotx.cap.SASLCapHandler;

import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationLoader {
    private static final Path configDir = Paths.get(".");
    private static final Logger logger = LogManager.getLogger("ConfigurationLoader");
    private final Gson GSON;

    private BotConfig botConfig = null;
    private Bot bot;

    public ConfigurationLoader(Bot bot) {
        this.bot = bot;
        this.GSON = new GsonBuilder()
                .registerTypeAdapter(BotConfig.class, new BotConfig.Default(bot))
                .setPrettyPrinting()
                .create();
    }


    public BotConfig getBotConfig() {
        return this.botConfig;
    }

    public Configuration build() {
        // User config
        Configuration.Builder config = new Configuration.Builder()
                .setName(this.botConfig.nick)
                .setRealName(this.botConfig.realname)
                .setLogin(this.botConfig.nick) // identity
                .addServer(this.botConfig.serverHost, this.botConfig.serverPort)
                .addAutoJoinChannels(this.botConfig.channels);

        // Optional stuff
        if (!this.botConfig.saslUser.isEmpty() && !this.botConfig.saslPass.isEmpty()) {
            config.addCapHandler(new SASLCapHandler(this.botConfig.saslUser, this.botConfig.saslPass));
        }
        else if (!this.botConfig.nickServPass.isEmpty()) {
            config.setNickservNick(this.botConfig.nick);
            config.setNickservPassword(this.botConfig.nickServPass);
        }

        if (!this.botConfig.debugChannel.isEmpty()) {
            config.addAutoJoinChannel(this.botConfig.debugChannel);
        }


        // Our finishing touches
        return config.setSocketFactory(SSLSocketFactory.getDefault())
                .setAutoNickChange(true)
                .addListener(new EventListener(this.bot))
                .setVersion("Jamesbot v" + BuildInfo.version)
                .setAutoReconnect(true)
                .setAutoReconnectAttempts(20)
                .setAutoReconnectDelay(2000)
                .buildConfiguration();
    }

    public void load() throws IOException {
        File configFile = Paths.get(configDir.toString(), "config.json").toFile();
        if (configFile.createNewFile()) {
            logger.info("Creating new config file {}", configFile.toString());
        }
        else {
            logger.info("Loading config file from {}", configFile.toString());
        }

        Reader reader = new FileReader(configFile);
        this.botConfig = GSON.fromJson(reader, BotConfig.class);

        if (this.botConfig == null) {
            this.botConfig = GSON.fromJson("{}", BotConfig.class);
        }

        this.save();
    }

    public void save() throws IOException {
        File configFile = Paths.get(configDir.toString(), "config.json").toFile();

        Writer writer = new FileWriter(configFile);
        GSON.toJson(this.botConfig, writer);
        writer.close();
    }
}
