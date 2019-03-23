package net.polarizedions.jamesbot.modules.fun;

import net.polarizedions.jamesbot.config.BotConfig;
import net.polarizedions.jamesbot.core.Bot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pircbotx.hooks.events.MessageEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiceTest {

    @BeforeEach
    void resetRandomSeed() {
        Dice.RANDOM.setSeed(0L);
    }

    @Test
    void oneRollResponse() {
        Bot mockBot = mockBot();
        MessageEvent mockEvent = mock(MessageEvent.class);
        when(mockEvent.getMessage()).thenReturn("!1d8");

        assertTrue(new Dice(mockBot).run(mockEvent));

        verify(mockEvent).respondWith("Rolled 1 d8 dice and got 6.");
    }

    @Test
    void smallRollResponse() {
        Bot mockBot = mockBot();
        MessageEvent mockEvent = mock(MessageEvent.class);
        when(mockEvent.getMessage()).thenReturn("!8d8");

        assertTrue(new Dice(mockBot).run(mockEvent));

        verify(mockEvent).respondWith(contains("35"));
    }

    @Test
    void largeRollResponse() {
        Bot mockBot = mockBot();
        MessageEvent mockEvent = mock(MessageEvent.class);
        when(mockEvent.getMessage()).thenReturn("!100d8");

        assertTrue(new Dice(mockBot).run(mockEvent));

        verify(mockEvent).respondWith(contains("468"));
    }

    @Test
    void nonCommand() {
        Bot mockBot = mockBot();
        MessageEvent mockEvent = mock(MessageEvent.class);
        when(mockEvent.getMessage()).thenReturn("Watch your hands, sneak thief!");

        assertFalse(new Dice(mockBot).run(mockEvent));

        verify(mockEvent, never()).respondWith(anyString());
    }

    @Test
    void smallRoll() {
        assertEquals(42, new Dice(mockBot()).roll(10, 8));
    }

    @Test
    void bigRoll() {
        assertEquals(52366, new Dice(mockBot()).roll(1000, 100));
    }


    Bot mockBot() {
        BotConfig mockConfig = mock(BotConfig.class);
        mockConfig.commandPrefix = "!";

        Bot mockBot = mock(Bot.class);
        when(mockBot.getBotConfig()).thenReturn(mockConfig);

        return mockBot;
    }
}