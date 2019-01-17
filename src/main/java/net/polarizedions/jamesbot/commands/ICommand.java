package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import org.pircbotx.hooks.events.MessageEvent;

public interface ICommand {
    void register(CommandDispatcher<MessageEvent> dispatcher);

    String getHelp();

    String getUsage();
}
