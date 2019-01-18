package net.polarizedions.jamesbot.config;

import com.google.gson.InstanceCreator;

import java.lang.reflect.Type;

public class ConfigAPIKeys {
    public String youtube;

    static class Default extends ConfigAPIKeys {
        {
            youtube = "";
        }
    }
}
