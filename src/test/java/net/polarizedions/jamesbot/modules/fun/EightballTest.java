package net.polarizedions.jamesbot.modules.fun;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.polarizedions.jamesbot.config.BotConfig;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.utils.CommandMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EightballTest {

    @Test
    void registerCommand() {
        CommandDispatcher<CommandMessage> dispatcher = new CommandDispatcher<>();
        Eightball eb = new Eightball(mockBot());

        eb.register(dispatcher);

        // 2 because it registers an alias too
        assertEquals(2, dispatcher.getRoot().getChildren().size());
    }

    @Test
    void produceOutput() {
        CommandMessage cmdMsg = mock(CommandMessage.class);

        Eightball eb = new Eightball(mockBot());
        eb.eightball(cmdMsg);

        verify(cmdMsg).respondWith(anyString());
    }

    @Test
    void runCommand() throws CommandSyntaxException {
        CommandDispatcher<CommandMessage> dispatcher = new CommandDispatcher<>();
        Eightball eb = new Eightball(mockBot());
        CommandMessage cmdMsg = mock(CommandMessage.class);

        eb.register(dispatcher);

        dispatcher.execute("8ball are you still there?", cmdMsg);

        verify(cmdMsg).respondWith(anyString());
    }

    @Test
    void dontRunCommand() {
        CommandDispatcher<CommandMessage> dispatcher = new CommandDispatcher<>();
        Eightball eb = new Eightball(mockBot());
        CommandMessage cmdMsg = mock(CommandMessage.class);

        eb.register(dispatcher);

        assertThrows(CommandSyntaxException.class, () -> dispatcher.execute("Fus Ro Da!", cmdMsg));

        verify(cmdMsg, never()).respondWith(anyString());
    }


    Bot mockBot() {
        BotConfig mockConfig = mock(BotConfig.class);
        mockConfig.commandPrefix = "!";

        Bot mockBot = mock(Bot.class);
        when(mockBot.getBotConfig()).thenReturn(mockConfig);

        return mockBot;
    }
}