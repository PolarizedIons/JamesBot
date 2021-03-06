package net.polarizedions.jamesbot.modules.core;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.commands.ICommand;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.utils.CommandMessage;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class JoinPart extends Module implements ICommand {
    public JoinPart(Bot bot) {
        super(bot);
    }

    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("join")
                        .requires(Bot::staffCommandRequirement)
                        .then(
                                argument("channel", greedyString()).executes(c -> this.join(getString(c, "channel")))
                        )
        );

        dispatcher.register(
                literal("part")
                        .requires(Bot::staffCommandRequirement)
                        .then(
                                argument("channel", greedyString()).executes(c -> this.part(getString(c, "channel")))
                        )
        );
    }

    private int join(String channel) {
        channel = channel.split("\\s")[0];

        this.bot.getPircBot().sendIRC().joinChannel(channel);
        this.bot.getBotConfig().channels.add(channel);
        this.bot.saveBotConfig();

        return ReturnConstants.SUCCESS;
    }

    private int part(String channel) {
        channel = channel.split("\\s")[0];

        // There's no part method?!
        this.bot.getPircBot().sendRaw().rawLine("PART " + channel + " :Bai");
        this.bot.getBotConfig().channels.remove(channel);
        this.bot.saveBotConfig();

        return ReturnConstants.SUCCESS;
    }

    @Override
    public String getHelp() {
        return "Join/part channels";
    }

    @Override
    public String getUsage() {
        return "join <channel>, part <channel>";
    }

    @Override
    public String getModuleName() {
        return "joinpart";
    }
}
