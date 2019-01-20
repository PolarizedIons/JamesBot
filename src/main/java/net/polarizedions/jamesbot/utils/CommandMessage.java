package net.polarizedions.jamesbot.utils;

import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandMessage {
    private static final Pattern TARGETED_PATTERN = Pattern.compile(".*@([,\\sA-Za-z0-9.+!#&[]`_^{}|-]]+)$", Pattern.MULTILINE);

    MessageEvent wrapped;
    String target;
    String message;

    public CommandMessage(MessageEvent msg) {
        this(msg, msg.getMessage().startsWith(Bot.instance.getBotConfig().commandPrefix) ? msg.getMessage().substring(Bot.instance.getBotConfig().commandPrefix.length()) : msg.getMessage());
    }

    public CommandMessage(MessageEvent msg, String message) {
        this.wrapped = msg;
        this.message = message;

        this.determineTarget();
        System.out.println("I AM A NEW MESSAGE" + this.message + " with target " + this.target);
    }

    private void determineTarget() {
        Matcher matcher = TARGETED_PATTERN.matcher(this.getMessage());
        boolean matches = matcher.matches();

        this.target = matches ? matcher.group(1) : this.getNick();

        if (matches) {
            this.message = this.message.substring(0, this.message.lastIndexOf('@')).trim();
        }
    }

    public MessageEvent getWrapped() {
        return this.wrapped;
    }

    public String getMessage() {
        return this.message;
    }

    public String getChannel() {
        return this.wrapped.getChannel().getName();
    }

    public void respond(String response) {
        this.respondWith(this.getTarget() + ": " + response);
    }

    public void respondWith(String response) {
        this.wrapped.respondWith(response);
    }

    public User getUser() {
        return this.wrapped.getUser();
    }

    public String getNick() {
        User user = this.getUser();
        return user == null ? null : user.getNick();
    }

    public String getTarget() {
        return target;
    }

    public void notice(String content) {
        this.noticeWith(this.getTarget() + ": " + content);
    }

    public void noticeWith(String content) {
        this.wrapped.getBot().sendIRC().notice(this.wrapped.getChannel().getName(), content);
    }

    public void noticePM(String content) {
        this.wrapped.getBot().sendIRC().notice(this.getTarget(), content);
    }

    public void action(MessageEvent msg, String content) {
        msg.getBot().sendIRC().action(msg.getChannel().getName(), content);
    }

}
