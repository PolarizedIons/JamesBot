package net.polarizedions.jamesbot.config;

public class DatabaseConfig {
    public String host;
    public long port;
    public String username;
    public String password;
    public String database;

    static class Default extends DatabaseConfig {
        {
            host = "localhost";
            port = 27017;
            username = "";
            password = "";
            database = "jamesbot";
        }
    }
}
