package net.polarizedions.jamesbot.utils;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

public class CommandMessage {

    MessageEvent wrapped;
    public CommandMessage(MessageEvent msg) {
        this.wrapped = msg;
    }

    public MessageEvent getWrapped() {
        return this.wrapped;
    }

    public String getMessage() {
        return this.wrapped.getMessage();
    }

    public String getChannel() {
        return this.wrapped.getChannelSource();
    }

    public void respond(String response) {
        this.wrapped.respond(response);
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

    public void notice(String content) {
        this.noticeWith(this.wrapped.getUser().getNick() + ": " + content);
    }

    public void noticeWith(String content) {
        this.wrapped.getBot().sendIRC().notice(this.wrapped.getChannel().getName(), content);
    }

    public void noticePM(String content) {
        this.wrapped.getBot().sendIRC().notice(this.wrapped.getUser().getNick(), content);
    }

    public void action(MessageEvent msg, String content) {
        msg.getBot().sendIRC().action(msg.getChannel().getName(), content);
    }

}
