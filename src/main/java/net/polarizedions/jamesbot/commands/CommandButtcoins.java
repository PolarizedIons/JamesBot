package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.database.ButtcoinAccount;
import net.polarizedions.jamesbot.utils.CommandMessage;

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
        Bot.instance.getButtcoinAPI().activateAccount(source.getNick());
        return 0;
    }

    private int getStats(CommandMessage source, String nick) {
        ButtcoinAccount account = Bot.instance.getButtcoinAPI().getAccount(nick);

        String queryNick = nick.equalsIgnoreCase(source.getNick()) ? "You have " : nick + " has ";
        source.respond(queryNick + "an " + (account.active ? "active" : "inactive") + " account, with " + account.balance + " buttcoins (" + account.mined + " mined, and " + account.bruteforced + " of which was bruteforced.) They've gifted " + account.gifted + " and received " + account.given + " buttcoins");
        return ReturnConstants.SUCCESS;
    }

    private int transfer(CommandMessage source, int amount, String toNick, String reason) {
        if (source.getNick().equalsIgnoreCase(toNick)) {
            source.noticePM("You are " + toNick + "!");
            return ReturnConstants.FAIL_SILENT;
        }

        if (amount <= 0) {
            source.noticePM("You must send at least 1 buttcoin!");
            return ReturnConstants.FAIL_SILENT;
        }

        if (!Bot.instance.getButtcoinAPI().isAccountActive(toNick)) {
            source.noticePM("Sorry, " + toNick + " does not have an active account");
            return ReturnConstants.FAIL_SILENT;
        }

        if (!Bot.instance.getButtcoinAPI().transfer(source.getNick(), toNick, amount)) {
            source.noticePM("I couldn't do that. Do you have enough buttcoins?");
            return ReturnConstants.FAIL_SILENT;
        }

        ButtcoinAccount fromAccount = Bot.instance.getButtcoinAPI().getAccount(source.getNick());
        ButtcoinAccount toAccount = Bot.instance.getButtcoinAPI().getAccount(toNick);
        source.noticePM(String.format("[TRANSFER] You (%d) have sent %d buttcoins to %s (%d )", fromAccount.balance, amount, toNick, toAccount.balance));
        Bot.noticePM(toNick, String.format("You (%d) have received %d buttcoins from %s (%d) [%s]", toAccount.balance, amount, source.getNick(), fromAccount.balance, reason));

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
