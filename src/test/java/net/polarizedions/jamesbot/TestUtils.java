package net.polarizedions.jamesbot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TestUtils {

    public static User createUser(String nick, String ident, String host) {
        String hostmask = nick + "!" + ident + "@" + host;

        UserHostmask uhm = createInstance(UserHostmask.class, new Class[] { PircBotX.class, String.class }, new Object[] { null, hostmask });
        return createInstance(User.class, new Class[] { UserHostmask.class }, new Object[] { uhm });
    }

    @Nullable
    public static <T> T createInstance(@NotNull Class<T> clazz, Class[] argTypes, Object[] args) {
        try {
            Constructor<T> constructor = clazz.getDeclaredConstructor(argTypes);
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("Couldn't create instance of " + clazz);
            e.printStackTrace(System.err);
            return null;
        }

    }
}
