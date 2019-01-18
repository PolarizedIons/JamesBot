package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.utils.CommandMessage;

import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class CommandPing implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("ping")
                        .executes(this::ping)
        );
    }

    private int ping(CommandContext<CommandMessage> context) {
        CommandMessage msg = context.getSource();
        msg.respondWith("pong!");
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
