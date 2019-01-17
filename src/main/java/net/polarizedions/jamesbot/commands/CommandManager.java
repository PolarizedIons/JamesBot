package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private static final Logger logger = LogManager.getLogger("CommandManager");
    private CommandDispatcher<MessageEvent> dispatcher;

    private List<ICommand> commands;

    public CommandManager() {
        this.dispatcher = new CommandDispatcher<>();
        this.commands = new ArrayList<>();

        this.commands.add(new CommandPing());
        this.commands.add(new CommandTemp());
        this.commands.add(new CommandMemes());


        for (ICommand cmd : this.commands) {
            cmd.register(this.dispatcher);
        }
    }

    public boolean dispatch(String message, MessageEvent source) {
        try {
            return ReturnConstants.SUCCESS == dispatcher.execute(message, source);
        }
        catch (CommandSyntaxException e) {
            if (e.getCursor() != 0) {
                logger.error("Error handling command " + source.getMessage(), ": {}", e);
            }

            return false;
        }
    }
}
