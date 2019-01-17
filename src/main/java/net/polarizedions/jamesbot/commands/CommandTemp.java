package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import org.pircbotx.hooks.events.MessageEvent;

import javax.swing.text.NumberFormatter;

import java.text.DecimalFormat;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class CommandTemp implements ICommand {
    private static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("#.##");

    @Override
    public void register(CommandDispatcher<MessageEvent> dispatcher) {
        dispatcher.register(
                literal("ctf").then(
                        argument("temp", doubleArg())
                                .executes(c -> this.ctf(c.getSource(), getDouble(c, "temp")))
                )
        );

        dispatcher.register(
                literal("ftc").then(
                        argument("temp", doubleArg())
                            .executes(c -> this.ftc(c.getSource(), getDouble(c, "temp")))
                )
        );
    }

    private int ctf(MessageEvent source, double temp) {
        Bot.noticeReply(source, NUMBER_FORMATTER.format(temp) + "°C = " + NUMBER_FORMATTER.format((9.0/5.0) * temp + 32) + "°F");
        return ReturnConstants.SUCCESS;
    }


    private int ftc(MessageEvent source, double temp) {
        Bot.noticeReply(source, NUMBER_FORMATTER.format(temp) + "°F = " + NUMBER_FORMATTER.format((5.0/9.0) * (temp - 32)) + "°C");
        return ReturnConstants.SUCCESS;
    }

    @Override
    public String getHelp() {
        return "Convert between Celsius and Fahrenheit";
    }

    @Override
    public String getUsage() {
        return "<ctf|ftc> <temp>";
    }
}
