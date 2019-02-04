package net.polarizedions.jamesbot.responders;

import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Random;
import java.util.regex.Pattern;

public class ButtcoinCollector implements IResponder {
    private static final Pattern[] BUTTCOIN_SECRIT_WORDS = new Pattern[] {
            Pattern.compile("\\bthe\\b"),
            Pattern.compile("\\bthat\\b"),
            Pattern.compile("\\ba\\b"),
            Pattern.compile("\\band\\b"),
            Pattern.compile("\\bfor\\b")
    };
    private static final Random RANDOM = new Random();

    private Pattern nextWord = BUTTCOIN_SECRIT_WORDS[RANDOM.nextInt(BUTTCOIN_SECRIT_WORDS.length)];

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

        Bot.instance.debug(String.format("[BUTTCOIN] %s\u200B%s mined 1 buttcoin from %s (bruteforced? %s)", msg.getUser().getNick().substring(0, 1), msg.getUser().getNick().substring(1), this.nextWord, bruteforced));
        Bot.instance.getButtcoinAPI().mine(msg.getUser().getNick(), bruteforced);
        this.chooseNewWord();
        Bot.instance.debug("[BUTTCOIN] New Word: " + this.nextWord);

        // Make sure other things can run too - we only observe
        return false;
    }

    private void chooseNewWord() {
        this.nextWord = BUTTCOIN_SECRIT_WORDS[RANDOM.nextInt(BUTTCOIN_SECRIT_WORDS.length)];
    }
}
