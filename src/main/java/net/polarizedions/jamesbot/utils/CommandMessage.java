package net.polarizedions.jamesbot.utils;

import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandMessage {
    private static final Pattern TARGETED_PATTERN = Pattern.compile(".*@([,\\sA-Za-z0-9.+!'\"#&[]`_^{}|-]]+)$", Pattern.MULTILINE);

    Bot bot;
    GenericMessageEvent wrapped;
    String target;
    String message;

    public CommandMessage(Bot bot, PrivateMessageEvent msg) {
        this(bot, msg, msg.getMessage());
    }

    public CommandMessage(Bot bot, MessageEvent msg) {
        this(bot, msg, msg.getMessage().startsWith(bot.getBotConfig().commandPrefix) ? msg.getMessage().substring(bot.getBotConfig().commandPrefix.length()) : msg.getMessage());
    }

    public CommandMessage(Bot bot, GenericMessageEvent msg, String message) {
        this.bot = bot;
        this.wrapped = msg;
        this.message = message;

        this.determineTarget();
    }

    private void determineTarget() {
        Matcher matcher = TARGETED_PATTERN.matcher(this.getMessage());
        boolean matches = matcher.matches();

        this.target = matches ? matcher.group(1) : this.getNick();

        if (matches) {
            this.message = this.message.substring(0, this.message.lastIndexOf('@')).trim();
        }
    }

    public Bot getBot() {
        return this.bot;
    }

    public String getMessage() {
        return this.message;
    }

    public String getNick() {
        User user = this.getUser();
        return user == null ? null : user.getNick();
    }

    public User getUser() {
        return this.wrapped.getUser();
    }

    public GenericMessageEvent getWrapped() {
        return this.wrapped;
    }

    public String getChannel() {
        if (this.wrapped instanceof MessageEvent) {
            return ( (MessageEvent)this.wrapped ).getChannel().getName();
        }
        else if (this.wrapped instanceof PrivateMessageEvent) {
            return this.wrapped.getUser().toString();
        }

        return "";
    }

    public void respond(String response) {
        this.respondWith(this.getTarget() + ": " + response);
    }

    public void respondWith(String response) {
        this.wrapped.respondWith(response);
    }

    public String getTarget() {
        return target;
    }

    public void noticePM(String content) {
        this.wrapped.getBot().sendIRC().notice(this.getTarget(), content);
    }

    public void action(MessageEvent msg, String content) {
        msg.getBot().sendIRC().action(msg.getChannel().getName(), content);
    }

}
