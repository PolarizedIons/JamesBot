package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.utils.CommandMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandManager {
    private static final Logger logger = LogManager.getLogger("CommandManager");
    private Bot bot;
    private CommandDispatcher<CommandMessage> dispatcher;

    public CommandManager(Bot bot) {
        this.bot = bot;
        this.dispatcher = new CommandDispatcher<>();

        for (Module cmd : this.bot.getModuleManager().getModules(ICommand.class)) {
            if (cmd.isActive()) {
                ((ICommand)cmd).register(this.dispatcher);
            }
        }
    }

    public boolean dispatch(CommandMessage source) {
        try {
            int result = dispatcher.execute(source.getMessage(), source);

            if (result == ReturnConstants.FAIL_LOG) {
                this.bot.debug(String.format("[Command Failed] %s/%s: %s", source.getChannel(), source.getNick(), source.getMessage()));
            }

            return result == ReturnConstants.SUCCESS || result == ReturnConstants.FAIL_REPLIED;
        }
        catch (CommandSyntaxException e) {
            if (e.getCursor() != 0) {
                logger.error("Error handling command " + source.getMessage(), ": {}", e);
            }
            else {
                logger.debug("unknown command");
            }
            return false;
        }
    }
}
