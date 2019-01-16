package net.polarizedions.jamesbot.core;

import org.pircbotx.Configuration;

public class BotConfig {

    // TODO: temp
    public static Configuration build() {
        return new Configuration.Builder()
                .setName("Jamesbot")
                .addServer("irc.freenode.net")
                .addAutoJoinChannel("##polarizedspam")
                .addListener(new EventListener())
                .buildConfiguration();
    }
}
