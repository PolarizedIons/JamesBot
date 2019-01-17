package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import org.pircbotx.hooks.events.MessageEvent;

import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class CommandPing implements ICommand {
    @Override
    public void register(CommandDispatcher<MessageEvent> dispatcher) {
        dispatcher.register(
                literal("ping")
                        .executes(this::ping)
        );
    }

    private int ping(CommandContext<MessageEvent> context) {
        MessageEvent msg = context.getSource();
        msg.getBot().sendIRC().notice(msg.getChannelSource(), msg.getUser().getNick() + ": pong!");

        return ReturnConstants.SUCCESS;
    }

    @Override
    public String getHelp() {
        return "Pings all your pongs!";
    }

    @Override
    public String getUsage() {
        return "ping";
    }
}
