package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.utils.CommandMessage;

import java.util.Random;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class CommandEightball implements ICommand {
    private static final Random RAND = new Random();
    private static final String[] RESPONSES = new String[] {
            "As I see it, yes.",
            "It is certain.",
            "It is decidedly so.",
            "Most likely.",
            "Outlook good.",
            "Signs point to yes.",
            "Without a doubt.",
            "Yes.",
            "Yes — definitely.",
            "You may rely on it.",
            "Reply hazy. Try again.",
            "Ask again later.",
            "Better not tell you now.",
            "Cannot predict now.",
            "Concentrate and ask again.",
            "Don\"t count on it.",
            "My reply is no.",
            "My sources say no.",
            "Outlook not so good.",
            "Very doubtful.",
    };

    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(literal("8ball").then(argument("q", greedyString()).executes(c -> this.eightball(c.getSource()))));
        dispatcher.register(literal("eightball").then(argument("q", greedyString()).executes(c -> this.eightball(c.getSource()))));
    }

    private int eightball(CommandMessage source) {
        source.respondWith(RESPONSES[RAND.nextInt(RESPONSES.length)]);
        return ReturnConstants.SUCCESS;
    }

    @Override
    public String getHelp() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }
}
