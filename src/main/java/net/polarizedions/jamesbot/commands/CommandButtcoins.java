package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.apis.Buttcoin;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.utils.CommandMessage;
import org.bson.Document;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;


public class CommandButtcoins implements ICommand {
    private static final int TIP_AMOUNT = 10;

    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("buttcoin")
                        .then(literal("activate").executes(c -> this.activate(c.getSource())))
                        .then(
                                literal("stats")
                                        .then(
                                                argument("nick", string()).executes(c -> this.getStats(c.getSource(), getString(c, "nick")))
                                        )
                                        .executes(c -> this.getStats(c.getSource(), c.getSource().getNick()))

                        )
                        .then(
                                literal("transfer").then(
                                        argument("amount", integer())
                                                .then(
                                                        argument("toNick", string())
                                                                .then(
                                                                        argument("reason", greedyString()).executes(c -> this.transfer(c.getSource(), getInteger(c, "amount"), getString(c, "toNick"), getString(c, "reason")))
                                                                )
                                                                .executes(c -> this.transfer(c.getSource(), getInteger(c, "amount"), getString(c, "toNick"), "No message provided."))
                                                )
                                )
                        )
                        .then(
                                literal("tip")
                                        .then(
                                                argument("nick", string())
                                                        .then(
                                                                argument("reason", greedyString()).executes(c -> this.transfer(c.getSource(), TIP_AMOUNT, getString(c, "nick"), getString(c, "reason")))
                                                        )
                                                        .executes(c -> this.transfer(c.getSource(), TIP_AMOUNT, getString(c, "nick"), "tip."))
                                        )
                        )
        );
    }

    private int activate(CommandMessage source) {
        Buttcoin.instance.activateAccount(source.getNick());
        return 0;
    }

    private int getStats(CommandMessage source, String nick) {
        Document account = Buttcoin.instance.getAccount(nick);
        boolean active = account.getBoolean("active");
        int balance = account.getInteger("balance");
        int mined = account.getInteger("mined");
        int bruteforced = account.getInteger("bruteforced");
        int gifted = account.getInteger("gifted");
        int given = account.getInteger("given");

        source.notice(nick + " has an " + (active ? "active" : "inactive") + " account, with " + balance + " buttcoins (" + mined + " mined, and " + bruteforced + " of which was bruteforced.) They've gifted " + gifted + " and received " + given + " buttcoins");
        return ReturnConstants.SUCCESS;
    }

    private int transfer(CommandMessage source, int amount, String toNick, String reason) {
        if (!Buttcoin.instance.transfer(source.getNick(), toNick, amount)) {
            source.noticePM("I couldn't do that. Do you have enough buttcoins?");
            return ReturnConstants.FAIL_SILENT;
        }

        source.noticePM("You transferred " + amount + " buttcoins to " + toNick + " with the message \"" + reason + "\"");
        if (Buttcoin.instance.isAccountActive(toNick)) {
            Bot.noticePM(toNick, source.getNick() + " has gifted you " + amount + " buttcoins [" + reason + "]");
        }

        return ReturnConstants.SUCCESS;
    }

    @Override
    public String getHelp() {
        return "Manage your buttcoins";
    }

    @Override
    public String getUsage() {
        return "buttcoin activate, buttcoin stats [user], buttcoin transfer (amount) [reason], buttcoin tip (user), (nickname)++";
    }
}
