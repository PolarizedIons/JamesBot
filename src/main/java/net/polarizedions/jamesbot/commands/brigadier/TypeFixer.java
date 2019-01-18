package net.polarizedions.jamesbot.commands.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.polarizedions.jamesbot.utils.CommandMessage;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class TypeFixer {
    // Thank you pokechu22 in #mcdevs on Freenode for helping me figure this out

    /**
     * Creates a new argument. Intended to be imported statically. The benefit of this over the brigadier {@link
     * LiteralArgumentBuilder#literal} method is that it is typed to {@link CommandMessage}.
     */
    @NotNull
    @Contract(pure = true)
    public static LiteralArgumentBuilder<CommandMessage> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Creates a new argument. Intended to be imported statically. The benefit of this over the brigadier {@link
     * RequiredArgumentBuilder#argument} method is that it is typed to {@link CommandMessage}.
     */
    @NotNull
    @Contract(pure = true)
    public static <T> RequiredArgumentBuilder<CommandMessage, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
