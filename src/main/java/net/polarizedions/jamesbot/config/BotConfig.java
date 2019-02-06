package net.polarizedions.jamesbot.config;

import com.google.gson.InstanceCreator;
import net.polarizedions.jamesbot.core.Bot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BotConfig {

    public String nick;
    public String realname;
    public String serverHost;
    public int serverPort;
    public Set<String> channels;
    public String debugChannel;
    public String commandPrefix;
    public List<StaffEntry> staff;

    public int memorySize;
    public Map<String, Boolean> enabledModules;

    public ConfigAPIKeys apiKeys;
    public DatabaseConfig databaseConfig;

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
            config.channels = new HashSet<>();
            config.channels.add("##PolarizedIons");
            config.debugChannel = "##PolarizedSpam";
            config.commandPrefix = "!";
            config.staff = new ArrayList<>();
            config.staff.add(new StaffEntry("", "", "unaffiliated/polarizedions"));

            config.memorySize = 250;
            config.enabledModules = Bot.instance.getModuleManager().getState();

            config.apiKeys = new ConfigAPIKeys.Default();
            config.databaseConfig = new DatabaseConfig.Default();

            config.saslUser = "";
            config.saslPass = "";
            config.nickServPass = "";

            return config;
        }
    }
}
