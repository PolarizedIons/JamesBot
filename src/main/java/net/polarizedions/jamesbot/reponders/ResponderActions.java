package net.polarizedions.jamesbot.reponders;

import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.hooks.events.ActionEvent;

public class ResponderActions implements IResponder {
    @Override
    public void run(ActionEvent msg) {
        String action = msg.getAction().toLowerCase().trim();
        String nick = Bot.instance.getPircBot().getNick().toLowerCase();

        if (action.endsWith("hugs " + nick)) {
            this.actHug(msg);
        }
        else if (action.contains("slaps " + nick + " around a bit")) {
            this.actNineties(msg);
        }
        else if (action.endsWith("pats " + nick) || action.endsWith("loves " + nick) || action.endsWith("pets " + nick)) {
            this.actLove(msg);
        }
        else if (action.endsWith("slaps " + nick)) {
            this.actSlapped(msg);
        }
        else if (action.endsWith("murders " + nick) || action.endsWith("kills " + nick) || action.endsWith("stabs " + nick)) {
            this.actKilled(msg);
        }
        else if (action.endsWith("licks " + nick) || action.endsWith("kisses " + nick)) {
            this.actPolice(msg);
        }
    }


    private void actHug(ActionEvent msg) {
        Bot.action(msg, "hugs " + msg.getUser().getNick());
    }

    private void actNineties(ActionEvent msg) {
        Bot.noticeReply(msg, "The 90s called. They want their IRC client back.");
    }

    private void actLove(ActionEvent msg) {
        Bot.noticeReply(msg, "♥");
    }

    private void actSlapped(ActionEvent msg) {
        Bot.noticeReply(msg, "I may have deserved that.");
    }

    private void actKilled(ActionEvent msg) {
        Bot.action(msg, "dies. Ripperonini in peace.");
    }

    private void actPolice(ActionEvent msg) {
        Bot.action(msg, "calls the police.");
    }
}
