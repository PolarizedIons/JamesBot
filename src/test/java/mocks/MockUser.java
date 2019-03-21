package mocks;

import org.pircbotx.User;

public class MockUser extends User {
    public MockUser(String nick, String ident, String host) {
        super(new MockUserHostmask(nick, ident, host));
    }
}
