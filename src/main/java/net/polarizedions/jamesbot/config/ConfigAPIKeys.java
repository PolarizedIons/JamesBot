package net.polarizedions.jamesbot.config;

public class ConfigAPIKeys {
    public String youtube;
    public String twitterApiKey;
    public String twitterApiSecret;

    static class Default extends ConfigAPIKeys {
        {
            youtube = "";
            twitterApiKey = "";
            twitterApiSecret = "";
        }
    }
}
