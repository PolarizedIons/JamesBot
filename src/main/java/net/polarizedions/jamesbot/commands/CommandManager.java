package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.utils.CommandMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private static final Logger logger = LogManager.getLogger("CommandManager");
    private CommandDispatcher<CommandMessage> dispatcher;

    private List<ICommand> commands;

    public CommandManager() {
        this.dispatcher = new CommandDispatcher<>();
        this.commands = new ArrayList<>();

        this.commands.add(new CommandPing());
        this.commands.add(new CommandTemp());
        this.commands.add(new CommandMemes());
        this.commands.add(new CommandEightball());
        this.commands.add(new CommandYoutube());
        this.commands.add(new CommandQuote());


        for (ICommand cmd : this.commands) {
            cmd.register(this.dispatcher);
        }
    }

    public boolean dispatch(String message, CommandMessage source) {
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
