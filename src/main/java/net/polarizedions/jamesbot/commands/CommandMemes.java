package net.polarizedions.jamesbot.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.utils.CommandMessage;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Random;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;

public class CommandMemes implements ICommand {
    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(literal("ausmerica").executes(c -> this.ausmerica(c.getSource())));
        dispatcher.register(literal("nikon").executes(c -> this.nikon(c.getSource())));
        dispatcher.register(literal("bep").executes(c -> this.bep(c.getSource())));

        dispatcher.register(
                literal("yuno").then(
                        argument("msg", greedyString()).executes(c -> this.yuno(c.getSource(), getString(c, "msg")))
                )
        );

        dispatcher.register(
                literal("dolan").then(
                        argument("target", string()).executes(c -> this.dolan(c.getSource(), getString(c, "target")))
                )
        );

        dispatcher.register(
                literal("gooby").then(
                        argument("target", string()).executes(c -> this.gooby(c.getSource(), getString(c, "target")))
                )
        );
    }

    private int ausmerica(CommandMessage source) {
        source.respondWith("Lemon lemon lemon lemon lemon lemon lemon lemon. http://i.imgur.com/5C4Gi.png");
        return ReturnConstants.SUCCESS;
    }

    private int nikon(CommandMessage source) {
        source.respondWith("http://i.imgur.com/nikon.png");
        return ReturnConstants.SUCCESS;
    }

    private int bep(CommandMessage source) {
        source.respondWith("ADD &BEP COMMAND NAO it go here http://i.imgur.com/BEPSY.png");
        return ReturnConstants.SUCCESS;
    }

    private int yuno(CommandMessage source, String msg) {
        msg = msg.replace("\\bme\\b", source.getNick());

        source.respondWith("ლ(ಠ益ಠლ) Y U NO " + msg + "?");
        return ReturnConstants.SUCCESS;
    }

    private int dolan(CommandMessage source, String target) {
        source.respondWith("fak u " + this.memify(target));
        return ReturnConstants.SUCCESS;
    }

    private int gooby(CommandMessage source, String target) {
        source.respondWith(this.memify(target) + " pls");
        return ReturnConstants.SUCCESS;
    }

    @NotNull
    private String memify(String input) {
        input = input.replace("\\s", "");

        String[] targets = input.split(",");
        String delim = "";
        StringBuilder result = new StringBuilder();
        Random rand = new Random();

        for (String gooby : targets) {
            LinkedList<Character> letters = new LinkedList<>();
            for (char c : gooby.toCharArray()) {
                letters.add(c);
            }

            StringBuilder name = new StringBuilder("" + letters.removeFirst());
            String lastLetter = "" + letters.removeLast();
            while (letters.size() > 0) {
                name.append(letters.remove(rand.nextInt(letters.size())));
            }

            result.append(delim).append(name).append(lastLetter);
            delim = " and ";
        }

        return result.toString();
    }

    @Override
    public String getHelp() {
        return "Various meme commands";
    }

    @Override
    public String getUsage() {
        return "(todo)";
    }
}
