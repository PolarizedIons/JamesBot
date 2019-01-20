package net.polarizedions.jamesbot.apis;

import com.mongodb.client.MongoCollection;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.database.ButtcoinAccount;
import net.polarizedions.jamesbot.database.Database;
import org.bson.Document;

import static com.mongodb.client.model.Filters.regex;

public class Buttcoin {
    public static final Buttcoin instance = new Buttcoin();

    private Buttcoin() {}

    public void mine(String nick, boolean bruteforced) {
        MongoCollection<ButtcoinAccount> coll = Bot.instance.getDatabase().getButtcoinCollection();

        ButtcoinAccount mined = this.getAccount(nick);

        Document newMined = new Document("balance", mined.balance + 1)
                .append("mined", mined.mined + 1)
                .append("bruteforced", mined.bruteforced + (bruteforced ? 1 : 0));

        coll.updateOne(regex("name", nick, "i"), new Document("$set", newMined));
    }

    public ButtcoinAccount getAccount(String nick) {
        MongoCollection<ButtcoinAccount> coll = Bot.instance.getDatabase().getButtcoinCollection();

        ButtcoinAccount account = coll.find(regex("name", nick, "i")).first();
        if (account == null) {
            account = this.createAccount(nick);
        }

        return account;
    }

    public ButtcoinAccount createAccount(String nick) {
        MongoCollection<ButtcoinAccount> coll = Bot.instance.getDatabase().getButtcoinCollection();

        ButtcoinAccount account = new ButtcoinAccount(nick);
        coll.insertOne(account);

        return account;
    }

    public boolean isAccountActive (String nick) {
        return this.getAccount(nick).active;
    }

    public void activateAccount(String nick) {
        MongoCollection<ButtcoinAccount> coll = Bot.instance.getDatabase().getButtcoinCollection();
        coll.updateOne(regex("name", nick, "i"), new Document("$set", new Document("active", true)));
    }

    public boolean transfer(String from, String to, int amount) {
        MongoCollection<ButtcoinAccount> coll = Bot.instance.getDatabase().getButtcoinCollection();

        ButtcoinAccount fromAccount = this.getAccount(from);
        ButtcoinAccount toAccount = this.getAccount(to);

        if (fromAccount.mined < amount) {
            return false;
        }

        Document updateFrom = new Document("balance", fromAccount.balance - amount)
                .append("gifted", fromAccount.gifted + amount);

        Document updateTo = new Document("balance", toAccount.balance + amount)
                .append("given", toAccount.given + amount);

        coll.updateOne(regex("name", from, "i"), new Document("$set", updateFrom));
        coll.updateOne(regex("name", to, "i"), new Document("$set", updateTo));

        return true;
    }
}
