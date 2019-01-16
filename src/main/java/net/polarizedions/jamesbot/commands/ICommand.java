package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import org.pircbotx.hooks.types.GenericMessageEvent;

public interface ICommand {
    void register(CommandDispatcher<GenericMessageEvent> dispatcher);

    String getHelp();

    String getUsage();
}
