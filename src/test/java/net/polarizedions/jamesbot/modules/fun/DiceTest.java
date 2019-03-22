package net.polarizedions.jamesbot.modules.fun;

import net.polarizedions.jamesbot.config.BotConfig;
import net.polarizedions.jamesbot.core.Bot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pircbotx.hooks.events.MessageEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiceTest {

    @BeforeEach
    void resetRandomSeed() {
        Dice.RANDOM.setSeed(0L);
    }

    @Test
    void oneRoll() {
        // Setup
        BotConfig mockConfig = mock(BotConfig.class);
        mockConfig.commandPrefix = "!";

        Bot mockBot = mock(Bot.class);
        when(mockBot.getBotConfig()).thenReturn(mockConfig);

        // Test
        MessageEvent mockEvent = mock(MessageEvent.class);
        when(mockEvent.getMessage()).thenReturn("!1d8");

        new Dice(mockBot).run(mockEvent);

        verify(mockEvent).respondWith("Rolled 1 d8 dice and got 6.");
    }

    @Test
    void smallRoll() {
        // Setup
        BotConfig mockConfig = mock(BotConfig.class);
        mockConfig.commandPrefix = "!";

        Bot mockBot = mock(Bot.class);
        when(mockBot.getBotConfig()).thenReturn(mockConfig);

        // Test
        MessageEvent mockEvent = mock(MessageEvent.class);
        when(mockEvent.getMessage()).thenReturn("!8d8");

        new Dice(mockBot).run(mockEvent);

        verify(mockEvent).respondWith("Rolled 8 d8 dice and got 35.");
    }

    @Test
    void largeRoll() {
        // Setup
        BotConfig mockConfig = mock(BotConfig.class);
        mockConfig.commandPrefix = "!";

        Bot mockBot = mock(Bot.class);
        when(mockBot.getBotConfig()).thenReturn(mockConfig);

        // Test
        MessageEvent mockEvent = mock(MessageEvent.class);
        when(mockEvent.getMessage()).thenReturn("!100d8");

        new Dice(mockBot).run(mockEvent);

        verify(mockEvent).respondWith("Rolled 100 d8 dice and got 468.");
    }
}