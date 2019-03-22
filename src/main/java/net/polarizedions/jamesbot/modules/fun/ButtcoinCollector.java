package net.polarizedions.jamesbot.modules.fun;

import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.responders.IResponder;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Random;
import java.util.regex.Pattern;

public class ButtcoinCollector extends Module implements IResponder {
    private static final int DOUBLE_CHANCE = 10_000;
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("\\bbutts?\\b");

    private static final Pattern[] BUTTCOIN_SECRIT_WORDS = new Pattern[] {
            Pattern.compile("\\bthe\\b"),
            Pattern.compile("\\bthat\\b"),
            Pattern.compile("\\ba\\b"),
            Pattern.compile("\\band\\b"),
            Pattern.compile("\\bfor\\b")
    };
    private static final Random RANDOM = new Random();

    private Pattern nextWord = this.chooseNewWord();

    public ButtcoinCollector(Bot bot) {
        super(bot);
    }

    @Override
    public boolean run(MessageEvent msg) {
        if (!nextWord.matcher(msg.getMessage()).find()) {
            return false;
        }

        boolean bruteforced = true;
        for (Pattern pattern : BUTTCOIN_SECRIT_WORDS) {
            if (!pattern.matcher(msg.getMessage()).find()) {
                bruteforced = false;
                break;
            }
        }

//        Bot.instance.debug(String.format("[BUTTCOIN] %s\u200B%s mined 1 buttcoin from %s (bruteforced? %s)", msg.getUser().getNick().substring(0, 1), msg.getUser().getNick().substring(1), this.nextWord, bruteforced));
        this.bot.getButtcoinAPI().mine(msg.getUser().getNick(), bruteforced);

        // Secret double mining
        if (RANDOM.nextInt(DOUBLE_CHANCE) == 1 && DOUBLE_PATTERN.matcher(msg.getMessage()).find()) {
//            Bot.instance.debug("[BUTTCOIN] %s\u200B%s mined 1 extra buttcoin from their butt usage." + msg.getUser().getNick().substring(0, 1), msg.getUser().getNick().substring(1));
            this.bot.getButtcoinAPI().mine(msg.getUser().getNick(), false);
        }

        this.nextWord = this.chooseNewWord();
//        Bot.instance.debug("[BUTTCOIN] New Word: " + this.nextWord);

        // Make sure other things can run too - we only observe
        return false;
    }

    private Pattern chooseNewWord() {
        return BUTTCOIN_SECRIT_WORDS[RANDOM.nextInt(BUTTCOIN_SECRIT_WORDS.length)];
    }

    @Override
    public String getModuleName() {
        return "buttcoin";
    }
}
