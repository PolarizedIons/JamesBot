package net.polarizedions.jamesbot.modules.chat;

import com.mojang.brigadier.CommandDispatcher;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import net.polarizedions.jamesbot.commands.ICommand;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.database.Quote;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.utils.CommandMessage;
import net.polarizedions.jamesbot.utils.FixedSizeQueue;
import org.bson.conversions.Bson;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.hooks.events.MessageEvent;

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
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Sorts.descending;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;


public class Quotes extends Module implements ICommand {
    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());

    public Quotes(Bot bot) {
        super(bot);
    }

    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        // remember (person) (search term)
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

    private int remember(@NotNull CommandMessage source, String nick, String search) {
        if (source.getNick().equalsIgnoreCase(nick)) {
            source.respond("You're not that memorable to me.");
            return ReturnConstants.FAIL_REPLIED;
        }

        MessageEvent found = this.searchMemory(source.getChannel(), nick, search);

        if (found == null) {
            source.respondWith(String.format("Can't find what %s said about %s", nick, search));
            return ReturnConstants.FAIL_REPLIED;
        }
        try {
            Quote quote = this.saveQuote(found);
            source.respond("Remembered: " + this.formatQuote(quote));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnConstants.SUCCESS;
    }

    private int getSpecificQuote(CommandMessage source, int number, String person) {
        MongoCollection<Quote> coll = this.bot.getDatabase().getQuoteCollection();

        Quote quote = coll.find(and(regex("nick", "^" + person + "$", "i"), eq("quoteNum", number))).first();
        if (quote == null) {
            source.respond("Sorry! I can't find that quote :(");
            return ReturnConstants.FAIL_REPLIED;
        }

        source.respond(this.formatQuote(quote));
        return ReturnConstants.SUCCESS;
    }

    private int getRandomQuote(CommandMessage source, @NotNull String person, String thing) {
        Bson filter = null;

        // Get quote from random person including "thing" in the message
        if (person.equals("*") && !thing.isEmpty()) {
            filter = regex("message", ".*\\b" + thing + "\\b.*", "i");
        }

        // Get quote from "person" saying something about "thing"
        else if (!person.isEmpty() && !thing.isEmpty()) {
            filter = and(regex("nick", "^" + person + "$", "i"), regex("message", ".*\\b" + thing + "\\b.*", "i"));
        }

        // Get a random quote from "person"
        else if (!person.isEmpty()) {
            filter = regex("nick", "^" + person + "$", "i");
        }

        // Get a random quote about "thing"
        else if (!thing.isEmpty()) {
            filter = regex("message", ".*\\b" + thing + "\\b.*", "i");
        }

        MongoCollection<Quote> coll = this.bot.getDatabase().getQuoteCollection();

        // TODO: find better solution
        List<Quote> docs = new ArrayList<>();
        FindIterable<Quote> quotes = filter == null ? coll.find() : coll.find(filter);
        quotes.iterator().forEachRemaining(docs::add);

        if (docs.size() == 0) {
            source.respond("Sorry, I couldn't find a quote matching that :(");
            return ReturnConstants.FAIL_REPLIED;
        }

        Quote quote = docs.get(RANDOM.nextInt(docs.size()));
        source.respond(this.formatQuote(quote));
        return ReturnConstants.SUCCESS;
    }

    private MessageEvent searchMemory(String channel, String nick, String search) {
        FixedSizeQueue<MessageEvent> memory = this.bot.getMessageMemory(channel);
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

    private Quote saveQuote(@NotNull MessageEvent message) {
        MongoCollection<Quote> col = this.bot.getDatabase().getQuoteCollection();

        Quote lastQuote = col.find(regex("nick", "^" + message.getUser().getNick() + "$", "i")).sort(descending("quoteNum")).first();
        int nextQuote = lastQuote == null ? 1 : lastQuote.quoteNum + 1;

        Quote quote = new Quote(message.getUser().getNick(), nextQuote, message.getMessage(), Date.from(Instant.now()));

        col.insertOne(quote);
        return quote;
    }

    private String formatQuote(Quote quote) {
        try {
            String formatted = quote.message + " - " + quote.nick + " #" + quote.quoteNum;

            if (quote.date != null) {
                formatted += " (" + DATE_FORMAT.format(quote.date.toInstant()) + ")";
            }
            return formatted;

        }
        catch (Exception e) {
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

    @Override
    public String getModuleName() {
        return "quote";
    }
}
