package net.polarizedions.jamesbot.modules.chat;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.commands.ICommand;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.utils.CommandMessage;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Date;

import static com.mojang.brigadier.arguments.DoubleArgumentType.doubleArg;
import static com.mojang.brigadier.arguments.DoubleArgumentType.getDouble;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class Time extends Module implements ICommand {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");

    public Time(Bot bot) {
        super(bot);
    }

    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("timestamp").executes(c -> this.unix(c.getSource()))
        );

        dispatcher.register(
                literal("time")
                        .then(
                                literal("utc").executes(c -> this.timeIn(c.getSource(), 0.0))
                        )
                        .then(
                                argument("offset", doubleArg()).executes(c -> this.timeIn(c.getSource(), getDouble(c, "offset")))
                        )
                        .then(
                                literal("unix").executes(c -> this.unix(c.getSource()))
                        )
                        .executes(c -> this.timeIn(c.getSource(), 0.0))
        );
    }

    public int unix(CommandMessage source) {
        source.respond(System.currentTimeMillis() / 1000 + "");

        return ReturnConstants.SUCCESS;
    }

    public int timeIn(CommandMessage source, Double offset) {
        String offsetStr = ( offset >= 0 ? "+" : "" ) + DECIMAL_FORMAT.format(offset);
        ZoneOffset currentOffset = ZoneOffset.systemDefault().getRules().getOffset(Instant.now());
        Date offsetTime = Date.from(Instant.ofEpochSecond((long)( Instant.now().getEpochSecond() + ( offset * 3600 ) - currentOffset.getTotalSeconds() )));

        source.respond(DATE_FORMAT.format(offsetTime) + " (UTC" + offsetStr + ")");
        return ReturnConstants.SUCCESS;
    }

    @Override
    public String getHelp() {
        return "Helps with the time";
    }

    @Override
    public String getUsage() {
        return "time [utc, unix, +7.5 (etc...)], timestamp";
    }

    @Override
    public String getModuleName() {
        return "time";
    }
}
