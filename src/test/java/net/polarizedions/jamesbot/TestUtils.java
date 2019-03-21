package net.polarizedions.jamesbot;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TestUtils {

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
