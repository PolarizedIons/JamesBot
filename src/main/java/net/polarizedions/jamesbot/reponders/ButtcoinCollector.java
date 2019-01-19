package net.polarizedions.jamesbot.reponders;

import com.mongodb.client.MongoCollection;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.database.Database;
import org.bson.Document;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Random;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.regex;

public class ButtcoinCollector implements IResponder {
    private static final Pattern[] BUTTCOIN_SECRIT_WORDS = new Pattern[]{
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
        if (! nextWord.matcher(msg.getMessage()).matches()) {
            return false;
        }

        boolean bruteforced = true;
        for (Pattern pattern : BUTTCOIN_SECRIT_WORDS) {
            if (! pattern.matcher(msg.getMessage()).matches()) {
                bruteforced = false;
                break;
            }
        }

        Bot.instance.debug("[BUTTCOIN] " + msg.getUser().getNick() + " mined 1 buttcoin from " + this.nextWord + " (bruteforced? " + bruteforced + ")");
        this.mine(msg.getUser().getNick(), bruteforced);
        this.chooseNewWord();
        Bot.instance.debug("[BUTTCOIN] New Word: " + this.nextWord);

        // Make sure other things can run too - we only observe
        return false;
    }

    private void chooseNewWord() {
        this.nextWord = BUTTCOIN_SECRIT_WORDS[RANDOM.nextInt(BUTTCOIN_SECRIT_WORDS.length)];
    }

    private void mine(String nick, boolean bruteforced) {
        Database db = Bot.instance.getDatabase();
        MongoCollection<Document> coll = db.getCollection("buttcoins");

        Document mined = coll.find(regex("name", nick, "i")).first();
        if (mined == null) {
            mined = new Document("name", nick)
            .append("mined", 0)
            .append("bruteforced", 0)
            .append("gifted", 0)
            .append("given", 0);

            coll.insertOne(mined);
        }

        // TODO: check if there's a better way?
        Document newMined = new Document("name", nick)
                .append("mined", mined.getInteger("mined") + 1)
                .append("bruteforced", mined.getInteger("bruteforced") + (bruteforced ? 1 : 0))
                .append("gifted", mined.getInteger("gifted"))
                .append("given", mined.getInteger("given"));

        coll.updateOne(regex("name", nick, "i"), newMined);
    }
}
