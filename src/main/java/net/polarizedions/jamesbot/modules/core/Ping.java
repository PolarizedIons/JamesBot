package net.polarizedions.jamesbot.modules.core;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.polarizedions.jamesbot.commands.ICommand;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.utils.CommandMessage;
import org.jetbrains.annotations.NotNull;

import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class Ping extends Module implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("ping").executes(this::ping)
        );
    }

    private int ping(@NotNull CommandContext<CommandMessage> context) {
        CommandMessage msg = context.getSource();
        msg.respond("pong!");
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

    @Override
    public String getModuleName() {
        return "ping";
    }
}
