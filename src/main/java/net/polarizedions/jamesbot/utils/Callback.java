package net.polarizedions.jamesbot.utils;

@FunctionalInterface
public interface Callback<T> {
    void callback(T result);
}
