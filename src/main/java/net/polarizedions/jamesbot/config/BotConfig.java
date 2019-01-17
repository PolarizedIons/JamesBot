package net.polarizedions.jamesbot.config;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BotConfig {

    public String nick;
    public String realname;
    public String serverHost;
    public int serverPort;
    public List<String> channels;
    public String commandPrefix;

    public int memorySize;

    // Optionals
    public String saslUser;
    public String saslPass;
    public String nickServPass;

    static class Default implements InstanceCreator<BotConfig> {
        @Override
        public BotConfig createInstance(Type type) {
            BotConfig config = new BotConfig();
            config.nick = "Jamesbot";
            config.realname = "Jamesbot by PolarizedIons";
            config.serverHost = "irc.freenode.net";
            config.serverPort = 6697;
            config.channels = new ArrayList<>();
            config.channels.add("##PolarizedSpam");
            config.commandPrefix = "!";

            config.memorySize = 250;

            config.saslUser = "";
            config.saslPass = "";
            config.nickServPass = "";

            return config;
        }
    }
}
