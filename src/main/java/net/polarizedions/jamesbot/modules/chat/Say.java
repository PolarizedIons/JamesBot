package net.polarizedions.jamesbot.modules.chat;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.commands.ICommand;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.utils.CommandMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class Say extends Module implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("say")
                        .requires(Bot::staffCommandRequirement)
                        .then(
                                argument("message", greedyString()).executes(c -> this.say(c.getSource(), getString(c, "message")))
                        )
        );

        dispatcher.register(
                literal("notice")
                        .requires(Bot::staffCommandRequirement)
                        .then(
                                argument("message", greedyString()).executes(c -> this.notice(c.getSource(), getString(c, "message")))
                        )
        );

        dispatcher.register(
                literal("act")
                        .requires(Bot::staffCommandRequirement)
                        .then(
                                argument("message", greedyString()).executes(c -> this.act(c.getSource(), getString(c, "message")))
                        )
        );
    }

    private int say(CommandMessage source, String message) {
        String[] channelMessage = this.splitChannelMessage(source, message);
        String channel = channelMessage[0];
        message = channelMessage[1];

        Bot.instance.getPircBot().sendIRC().message(channel, message);
        return ReturnConstants.SUCCESS;
    }

    private int notice(CommandMessage source, String message) {
        String[] channelMessage = this.splitChannelMessage(source, message);
        String channel = channelMessage[0];
        message = channelMessage[1];

        Bot.instance.getPircBot().sendIRC().notice(channel, message);
        return ReturnConstants.SUCCESS;
    }

    private int act(CommandMessage source, String message) {
        String[] channelMessage = this.splitChannelMessage(source, message);
        String channel = channelMessage[0];
        message = channelMessage[1];

        Bot.instance.getPircBot().sendIRC().action(channel, message);
        return ReturnConstants.SUCCESS;
    }

    @NotNull
    @Contract("_, _ -> new")
    private String[] splitChannelMessage(@NotNull CommandMessage source, @NotNull String message) {
        String[] splitMessage = message.split("\\s");
        String channel = splitMessage[0];
        message = String.join(" ", Arrays.copyOfRange(splitMessage, 1, splitMessage.length));

        return new String[] { channel, message };
    }

    @Override
    public String getHelp() {
        return "Say stuff";
    }

    @Override
    public String getUsage() {
        return "[say,notice,act] [channel] (message)";
    }

    @Override
    public String getModuleName() {
        return "say";
    }
}
