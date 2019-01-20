package net.polarizedions.jamesbot.apis;

import com.mongodb.client.MongoCollection;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.database.Database;
import org.bson.Document;

import static com.mongodb.client.model.Filters.regex;

public class Buttcoin {
    public static final Buttcoin instance = new Buttcoin();

    private Buttcoin() {}

    public void mine(String nick, boolean bruteforced) {
        Database db = Bot.instance.getDatabase();
        MongoCollection<Document> coll = db.getCollection("buttcoins");

        Document mined = this.getAccount(nick);

        Document newMined = new Document("balance", mined.getInteger("balance") + 1)
                .append("mined", mined.getInteger("mined") + 1)
                .append("bruteforced", mined.getInteger("bruteforced") + (bruteforced ? 1 : 0));

        coll.updateOne(regex("name", nick, "i"), new Document("$set", newMined));
    }

    public Document getAccount(String nick) {
        MongoCollection<Document> coll = Bot.instance.getDatabase().getCollection("buttcoins");

        Document account = coll.find(regex("name", nick, "i")).first();
        if (account == null) {
            account = this.createAccount(nick);
        }

        return account;
    }

    public Document createAccount(String nick) {
        MongoCollection<Document> coll = Bot.instance.getDatabase().getCollection("buttcoins");

        Document account = new org.bson.Document("name", nick)
                .append("active", false)
                .append("balance", 0)
                .append("mined", 0)
                .append("bruteforced", 0)
                .append("gifted", 0)
                .append("given", 0);

        coll.insertOne(account);
        return account;
    }

    public boolean isAccountActive (String nick) {
        return this.getAccount(nick).getBoolean("active");
    }

    public void activateAccount(String nick) {
        MongoCollection<Document> coll = Bot.instance.getDatabase().getCollection("buttcoins");
        coll.updateOne(regex("name", nick, "i"), new Document("$set", new Document("active", true)));
    }

    public boolean transfer(String from, String to, int amount) {
        MongoCollection<Document> coll = Bot.instance.getDatabase().getCollection("buttcoins");

        Document fromAccount = this.getAccount(from);
        Document toAccount = this.getAccount(to);

        if (fromAccount.getInteger("mined") < amount) {
            return false;
        }

        Document updateFrom = new Document("balance", fromAccount.getInteger("balance") - amount)
                .append("gifted", fromAccount.getInteger("gifted") + amount);

        Document updateTo = new Document("balance", toAccount.getInteger("balance") + amount)
                .append("given", toAccount.getInteger("given") + amount);

        coll.updateOne(regex("name", from, "i"), new Document("$set", updateFrom));
        coll.updateOne(regex("name", to, "i"), new Document("$set", updateTo));

        return true;
    }
}
