package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.ArrayList;
import java.util.List;

public class CommandManager {
    private static final Logger logger = LogManager.getLogger("CommandManager");
    private CommandDispatcher<GenericMessageEvent> dispatcher;

    private List<ICommand> commands;

    public CommandManager() {
        this.dispatcher = new CommandDispatcher<>();
        this.commands = new ArrayList<>();

        this.commands.add(new CommandPing());


        for (ICommand cmd : this.commands) {
            cmd.register(this.dispatcher);
        }
    }

    public boolean dispatch(GenericMessageEvent msg) {
        int prefixLen = Bot.instance.getBotConfig().commandPrefix.length();

        try {
            return ReturnConstants.SUCCESS == dispatcher.execute(msg.getMessage().substring(prefixLen), msg);
        }
        catch (CommandSyntaxException e) {
            if (e.getCursor() != 0) {
                logger.error("Error handling command " + msg.getMessage(), ": {}", e);
            }

            return false;
        }
    }
}
