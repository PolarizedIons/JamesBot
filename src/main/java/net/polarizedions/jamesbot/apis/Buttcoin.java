package net.polarizedions.jamesbot.apis;

import com.mongodb.client.MongoCollection;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.database.ButtcoinAccount;
import net.polarizedions.jamesbot.utils.Pair;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.mongodb.client.model.Filters.regex;

public class Buttcoin {

    @NotNull
    private Bson getNameRegex(String nick) {
        return regex("name", "^" + nick + "$", "i");
    }

    public ButtcoinAccount mine(String nick, boolean bruteforced) {
        MongoCollection<ButtcoinAccount> coll = Bot.instance.getDatabase().getButtcoinCollection();

        ButtcoinAccount account = this.getAccount(nick);

        Document updatedAccount = new Document("balance", account.balance + 1)
                .append("mined", account.mined + 1)
                .append("bruteforced", account.bruteforced + ( bruteforced ? 1 : 0 ));

        coll.updateOne(this.getNameRegex(nick), new Document("$set", updatedAccount));

        return this.getAccount(nick);
    }

    public ButtcoinAccount getAccount(String nick) {
        MongoCollection<ButtcoinAccount> coll = Bot.instance.getDatabase().getButtcoinCollection();

        ButtcoinAccount account = coll.find(this.getNameRegex(nick)).first();
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

    public boolean isAccountActive(String nick) {
        return this.getAccount(nick).active;
    }

    public void activateAccount(String nick) {
        MongoCollection<ButtcoinAccount> coll = Bot.instance.getDatabase().getButtcoinCollection();
        coll.updateOne(this.getNameRegex(nick), new Document("$set", new Document("active", true)));
    }

    @Nullable
    public Pair<ButtcoinAccount, ButtcoinAccount> transfer(String from, String to, int amount) {
        MongoCollection<ButtcoinAccount> coll = Bot.instance.getDatabase().getButtcoinCollection();

        ButtcoinAccount fromAccount = this.getAccount(from);
        ButtcoinAccount toAccount = this.getAccount(to);

        if (fromAccount.balance < amount) {
            return null;
        }

        Document updateFrom = new Document("balance", fromAccount.balance - amount)
                .append("gifted", fromAccount.gifted + amount);

        Document updateTo = new Document("balance", toAccount.balance + amount)
                .append("given", toAccount.given + amount);

        coll.updateOne(this.getNameRegex(from), new Document("$set", updateFrom));
        coll.updateOne(this.getNameRegex(to), new Document("$set", updateTo));

        return new Pair<>(this.getAccount(from), this.getAccount(to));
    }
}
