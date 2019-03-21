package net.polarizedions.jamesbot.config;

import mocks.MockUser;
import org.junit.jupiter.api.Test;
import org.pircbotx.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StaffEntryTest {
    private static User polarUser = new MockUser("PolarizedIons", "polar", "irc.polarizedions.net");

    @Test
    void matchesNick() {
        assertTrue(new StaffEntry("polarizedions", "*", "*").matches(polarUser));
        assertFalse(new StaffEntry("james", "*", "*").matches(polarUser));
    }

    @Test
    void matchesIdent() {
        assertTrue(new StaffEntry("*", "polar", "*").matches(polarUser));
        assertFalse(new StaffEntry("*", "james", "*").matches(polarUser));
    }

    @Test
    void matchesHost() {
        assertTrue(new StaffEntry("*", "*", "irc.polarizedions.net").matches(polarUser));
        assertFalse(new StaffEntry("*", "*", "123.telecom.net").matches(polarUser));
    }

    @Test
    void matchesFull() {
        assertTrue(new StaffEntry("polarizedions", "polar", "irc.polarizedions.net").matches(polarUser));
        assertFalse(new StaffEntry("james", "james", "123.telecom.net").matches(polarUser));
    }
}