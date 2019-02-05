package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.apis.Buttcoin;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.database.ButtcoinAccount;
import net.polarizedions.jamesbot.utils.CommandMessage;
import net.polarizedions.jamesbot.utils.Pair;
import org.jetbrains.annotations.NotNull;

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

    private int activate(@NotNull CommandMessage source) {
        Bot.instance.getButtcoinAPI().activateAccount(source.getNick());
        return 0;
    }

    private int getStats(@NotNull CommandMessage source, String nick) {
        ButtcoinAccount account = Bot.instance.getButtcoinAPI().getAccount(nick);

        if (source.getNick().equalsIgnoreCase(nick) && !account.isActive()) {
            Bot.instance.getButtcoinAPI().activateAccount(nick);
        }

        String queryNick = nick.equalsIgnoreCase(source.getNick()) ? "You have" : nick + " has";
        String queryNick2 = nick.equalsIgnoreCase(source.getNick()) ? "You've" : "They've";
        source.respond(String.format("%s an %s account, with %d buttcoins: %d mined, %d of which was bruteforced. %s gifted %d, and received %d buttcoins.", queryNick, account.active ? "active" : "inactive", account.balance, account.mined, account.bruteforced, queryNick2, account.gifted, account.given));
        return ReturnConstants.SUCCESS;
    }

    private int transfer(@NotNull CommandMessage source, int amount, String toNick, String reason) {
        String fromNick = source.getNick();
        Buttcoin buttcoinApi = Bot.instance.getButtcoinAPI();
        ButtcoinAccount fromAccount = buttcoinApi.getAccount(fromNick);
        ButtcoinAccount toAccount = buttcoinApi.getAccount(toNick);

        if (!fromAccount.isActive()) {
            buttcoinApi.activateAccount(fromNick);
        }

        if (source.getNick().equalsIgnoreCase(toNick)) {
            source.noticePM("You are " + toNick + "!");
            return ReturnConstants.FAIL_REPLIED;
        }

        if (amount <= 0) {
            source.noticePM("You must send at least 1 buttcoin!");
            return ReturnConstants.FAIL_REPLIED;
        }

        if (!toAccount.isActive()) {
            source.noticePM("Sorry, " + toNick + " does not have an active account");
            return ReturnConstants.FAIL_REPLIED;
        }

        if (fromAccount.balance < amount) {
            source.noticePM(String.format("Sorry, you (%s) do not have enough funds to transfer %s buttcoins", fromAccount.balance, amount));
            return ReturnConstants.FAIL_REPLIED;
        }

        Pair<ButtcoinAccount, ButtcoinAccount> result = buttcoinApi.transfer(fromNick, toNick, amount);
        if (result == null) {
            source.noticePM("I couldn't do that. Do you have enough buttcoins?");
            return ReturnConstants.FAIL_REPLIED;
        }

        fromAccount = result.getOne();
        toAccount = result.getTwo();

        source.noticePM(String.format("[TRANSFER] You (%d) have sent %d buttcoins to %s (%d) with the message: %s", fromAccount.balance, amount, toNick, toAccount.balance , reason));
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
