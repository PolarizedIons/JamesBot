package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mongodb.async.client.MongoCollection;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.utils.CommandMessage;
import org.bson.Document;
import org.pircbotx.hooks.events.MessageEvent;

import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;


public class CommandQuote implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(literal("quote").executes(this::quote));
        dispatcher.register(literal("remember").executes(this::remember));
    }

    private int remember(CommandContext<CommandMessage> messageEventCommandContext) {
        String channel = messageEventCommandContext.getSource().getChannel();
        MessageEvent msg = Bot.instance.getMessageMemory(channel).get(1);

        Document doc = new Document("user", msg.getUser().getNick())
            .append("message", msg.getMessage())
            .append("channel", msg.getChannelSource());

        Bot.instance.getDatabase().insert("quotes", doc, callback -> {
            System.out.println("success??? " + callback);
        });
        return 1;
    }

    private int quote(CommandContext<CommandMessage> objectCommandContext) {
        MongoCollection<Document> col = Bot.instance.getDatabase().getCollection("quotes");
        col.find().first((document, throwable) -> {
            if (throwable != null) {
                System.out.println("ERROR!!!");
                objectCommandContext.getSource().respondWith("error");
            }

            String user = document.getString("user");
            String message = document.getString("message");
            String channel = document.getString("channel");

            objectCommandContext.getSource().respondWith(channel +" - " + user + ": " + message);
        });
        return 1;
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
