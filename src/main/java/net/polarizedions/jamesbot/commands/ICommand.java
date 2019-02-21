package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.utils.CommandMessage;

public interface ICommand {
    void register(CommandDispatcher<CommandMessage> dispatcher);

    String getHelp();

    String getUsage();
}
