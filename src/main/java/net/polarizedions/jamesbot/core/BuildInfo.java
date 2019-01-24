package net.polarizedions.jamesbot.core;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class BuildInfo {
    public static String version = "Unknown";
    public static String buildtime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX").format(new Date());

    static {
        load();
    }

    private BuildInfo() {}

    static void load() {
        Properties buildInfo = new Properties();
        try {
            buildInfo.load(Bot.class.getResourceAsStream("/buildinfo.txt"));
        }
        catch (IOException ex) {
            Bot.logger.error("Error loading build information", ex);
        }

        Class<BuildInfo> clazz = BuildInfo.class;
        for (Field field : clazz.getFields()) {
            String value = buildInfo.getProperty(field.getName());
            if (value == null || value.startsWith("${") && value.endsWith("}")) {
                continue;
            }

            try {
                field.set(null, value);
            }
            catch (IllegalAccessException ex) {
                Bot.logger.error("Error setting build info value", ex);
            }
        }
    }
}
