package mocks;

import org.pircbotx.UserHostmask;

public class MockUserHostmask extends UserHostmask {
    public MockUserHostmask(String nick, String ident, String hostname) {
        super(null, nick + "!" + ident + "@" + hostname);
    }
}
