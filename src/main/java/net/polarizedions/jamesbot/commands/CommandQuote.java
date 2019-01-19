package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.database.Database;
import net.polarizedions.jamesbot.utils.CommandMessage;
import net.polarizedions.jamesbot.utils.FixedSizeQueue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.hooks.events.MessageEvent;

import javax.xml.crypto.Data;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static com.mongodb.client.model.Filters.all;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.regex;
import static net.polarizedions.jamesbot.commands.brigadier.ReturnConstants.FAIL_SILENT;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;


public class CommandQuote implements ICommand {
    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());

    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("remember")
                        .then(
                                argument("nick", string())
                                        .then(
                                                argument("search", greedyString())
                                                        .executes(c -> this.remember(c.getSource(), getString(c, "nick"), getString(c, "search")))
                                        )
                        )
        );

        // quote (number) (person)      => get quote #number from person
        // quote (person)               => get a random quote from person
        // quote                        => get a random quote
        // quote * (thing)              => get a random quote containing "thing"
        // quote (person) (thing)       => get a random quote from person containing "thing"
        dispatcher.register(
                literal("quote")
                        .then(
                                argument("number", integer()).then(
                                        argument("person", string()).executes(c -> this.getSpecificQuote(c.getSource(), getInteger(c, "number"), getString(c, "person")))
                                )
                        )
                        .then(
                                literal("*").then(
                                        argument("thing", greedyString()).executes(c -> this.getRandomQuote(c.getSource(), "", getString(c, "thing")))
                                )
                        )
                        .then(
                                argument("person", string())
                                        .then(
                                                argument("thing", greedyString()).executes(c -> this.getRandomQuote(c.getSource(), getString(c, "person"), getString(c, "thing")))
                                        )
                                        .executes(c -> this.getRandomQuote(c.getSource(), getString(c, "person"), ""))
                        )
                        .executes(c -> this.getRandomQuote(c.getSource(), "", ""))
        );
    }

    private int getRandomQuote(CommandMessage source, String person, String thing) {
        Bson filter = null;
        if (person.equals("*") && !thing.isEmpty()) {
            filter = regex("message", ".*\\b" + thing + "\\b.*", "i");
        }
        else if (!person.isEmpty() && !thing.isEmpty()) {
            filter = and(regex("nick", person, "i"), regex("message", ".*\\b" + thing + "\\b.*", "i"));
        }
        else if (!person.isEmpty()) {
            filter = regex("nick", person, "i");
        }
        else if (!thing.isEmpty()){
            filter = regex("message", ".*\\b" + thing + "\\b.*", "i");
        }

        Database db = Bot.instance.getDatabase();
        MongoCollection<Document> coll = db.getCollection("quotes");


        // TODO: find better solution
        List<Document> docs = new ArrayList<>();
        FindIterable<Document> quotes = filter == null ? coll.find() : coll.find(filter);
        quotes.iterator().forEachRemaining(docs::add);

        if (docs.size() == 0) {
            source.respond("Sorry, I couldn't find a quote matching that :(");
            return ReturnConstants.FAIL_SILENT;
        }

        Document quote = docs.get(RANDOM.nextInt(docs.size()));
        source.respond(this.formatQuote(quote));
        return ReturnConstants.SUCCESS;
    }

    private int getSpecificQuote(CommandMessage source, int number, String person) {
        Database db = Bot.instance.getDatabase();
        MongoCollection<Document> coll = db.getCollection("quotes");

        Document quote = coll.find(and(regex("nick", person, "i"), eq("quoteNum", number))).first();
        if (quote == null) {
            source.respond("Can't find that quote :(");
            return ReturnConstants.FAIL_SILENT;
        }

        source.respond(this.formatQuote(quote));
        return ReturnConstants.SUCCESS;
    }

    private int remember(CommandMessage source, String nick, String search) {
        if (source.getNick().equalsIgnoreCase(nick)) {
            source.respond("You're not that memorable to me.");
            return ReturnConstants.FAIL_SILENT;
        }

        MessageEvent found = this.searchMemory(source.getChannel(), nick, search);

        if (found == null) {
            source.respondWith("Can't find what " + nick + " said about " + search);
            return FAIL_SILENT;
        }
        try {
            Document quote = this.saveQuote(found);
            source.respond("Remembered: " + this.formatQuote(quote));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnConstants.SUCCESS;
    }

    private MessageEvent searchMemory(String channel, String nick, String search) {
        FixedSizeQueue<MessageEvent> memory = Bot.instance.getMessageMemory(channel);
        search = search.toLowerCase().replaceAll("\\.", "\\."); // Replace all . with \.

        for (int i = memory.size() - 1; i >= 0; i--) {
            MessageEvent msg = memory.get(i);

            if (msg.getUser() != null && msg.getUser().getNick().equalsIgnoreCase(nick)) {
                if (msg.getMessage().toLowerCase().matches(".*\\b" + search + "\\b.*")) {
                    return msg;
                }
            }
        }

        return null;
    }

    private Document saveQuote(@NotNull MessageEvent message) {
        Database db = Bot.instance.getDatabase();
        MongoCollection<Document> col = db.getCollection("quotes");
        long numQuotes = col.countDocuments(regex("nick", message.getUser().getNick(), "i")); // Case insensitive\

        Document quote = new Document("nick", message.getUser().getNick())
                .append("quoteNum", numQuotes + 1)
                .append("message", message.getMessage())
                .append("date", Date.from(Instant.now()));

        col.insertOne(quote);
        return quote;
    }

    private String formatQuote(Document quote) {
        try {
            String formatted = quote.getString("message") + " - " + quote.getString("nick") + " #" + quote.getLong("quoteNum");

            if (quote.get("date") != null) {
                Instant date = quote.getDate("date").toInstant();

                formatted += " (" + DATE_FORMAT.format(date) + ")";
            }
            return formatted;

        } catch (Exception e ) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String getHelp() {
        return "Manage channel quotes";
    }

    @Override
    public String getUsage() {
        return "quote (num) <person> OR quote * <words...> OR remember <person> <words>";
    }
}
