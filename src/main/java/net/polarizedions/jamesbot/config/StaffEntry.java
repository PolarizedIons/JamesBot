package net.polarizedions.jamesbot.config;

import org.pircbotx.User;

import java.util.regex.Pattern;

public class StaffEntry {
    public String nick;
    public String ident;
    public String host;

    public StaffEntry(String nick, String ident, String host) {
        this.nick = nick;
        this.ident = ident;
        this.host = host;
    }

    public boolean matches(User user) {
        String nick = this.nick.isEmpty() || this.nick.equals("*") ? ".*" : this.nick;
        String ident = this.ident.isEmpty() || this.ident.equals("*") ? ".*" : this.ident;
        String host = this.host.isEmpty() || this.host.equals("*") ? ".*" : this.host;

        Pattern thisEntry = Pattern.compile("^" + nick + "!" + ident + "@" + host + "$", Pattern.CASE_INSENSITIVE);
        String matchEntry = user.getNick() + "!" + user.getIdent() + "@" + user.getHostname();

        return thisEntry.matcher(matchEntry).find();
    }

}
