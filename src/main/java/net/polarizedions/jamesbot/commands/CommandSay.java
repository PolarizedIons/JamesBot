package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.utils.CommandMessage;

import java.util.Arrays;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class CommandSay implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("say").then(
                        argument("message", greedyString()).executes(c -> this.say(c.getSource(), getString(c, "message")))
                )
        );
    }

    private int say(CommandMessage source, String message) {
        String channel = source.getChannel();
        if (! Character.isAlphabetic(message.charAt(0))) {
            String[] splitMessage = message.split("\\s");
            channel = splitMessage[0];
            message = String.join(" ", Arrays.copyOfRange(splitMessage, 1, splitMessage.length));
        }

        Bot.instance.getPircBot().sendIRC().message(channel, message);

        return ReturnConstants.SUCCESS;
    }

    @Override
    public String getHelp() {
        return "Say stuff";
    }

    @Override
    public String getUsage() {
        return "say [channel] (message)";
    }
}
