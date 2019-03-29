package net.polarizedions.jamesbot.modules.chat;

import net.polarizedions.jamesbot.apis.apiutil.WebHelper;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.responders.IResponder;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FetchTitle extends Module implements IResponder {
    private static final Pattern TITLE_PATTERN = Pattern.compile(".*<title>((\\n|\\s|\\r|\\t|.)+)</title>.*");

    public FetchTitle(Bot bot) {
        super(bot);
    }

    @Override
    public boolean run(MessageEvent msg) {
        String prefix = this.bot.getBotConfig().commandPrefix;
        String message = msg.getMessage();
        if (!message.startsWith(prefix)) {
            return false;
        }
        message = message.substring(prefix.length());

        if (!message.startsWith("http")) {
            return false;
        }

        String html = WebHelper.fetchPart(message, 4096);
        if (html == null) {
            msg.respond("Sorry, I couldn't fetch that page!");
            return true;
        }

        String title = "";
        for (String line : html.split("[\r\n]")) {
            Matcher matcher = TITLE_PATTERN.matcher(line);
            if (matcher.find()) {
                title = matcher.group(1).replaceAll("[\\r\\n]", "");
                break;
            }
        }

        if (title.isEmpty()) {
            msg.respond("Sorry, I can't find the title!");
            return true;
        }

        msg.respond(title);
        return true;
    }

    @Override
    public String getModuleName() {
        return "title";
    }
}
