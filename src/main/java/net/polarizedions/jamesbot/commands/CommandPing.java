package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import org.jetbrains.annotations.NotNull;
import org.pircbotx.hooks.types.GenericMessageEvent;

import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class CommandPing implements ICommand {
    @Override
    public void register(CommandDispatcher<GenericMessageEvent> dispatcher) {
        dispatcher.register(
                literal("ping")
                        .executes(this::ping)
        );
    }

    private int ping(@NotNull CommandContext<GenericMessageEvent> context) {
        GenericMessageEvent msg = context.getSource();

        msg.respondWith("Pong!");

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
