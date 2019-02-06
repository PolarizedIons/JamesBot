package net.polarizedions.jamesbot.modules.chat;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.commands.ICommand;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.utils.CommandMessage;

import java.text.DecimalFormat;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class Temperature extends Module implements ICommand {
    private static final DecimalFormat NUMBER_FORMATTER = new DecimalFormat("#.##");

    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
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

    private int ctf(CommandMessage source, double temp) {
        source.respondWith(NUMBER_FORMATTER.format(temp) + "°C = " + NUMBER_FORMATTER.format(( 9.0 / 5.0 ) * temp + 32) + "°F");
        return ReturnConstants.SUCCESS;
    }


    private int ftc(CommandMessage source, double temp) {
        source.respondWith(NUMBER_FORMATTER.format(temp) + "°F = " + NUMBER_FORMATTER.format(( 5.0 / 9.0 ) * ( temp - 32 )) + "°C");
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

    @Override
    public String getModuleName() {
        return "temperature";
    }
}
