package net.polarizedions.jamesbot.modules.chat;

import com.mojang.brigadier.CommandDispatcher;
import net.polarizedions.jamesbot.apis.MojiraAPI;
import net.polarizedions.jamesbot.commands.ICommand;
import net.polarizedions.jamesbot.commands.brigadier.ReturnConstants;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.Module;
import net.polarizedions.jamesbot.utils.CommandMessage;
import net.polarizedions.jamesbot.utils.Pair;

import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static com.mojang.brigadier.arguments.StringArgumentType.string;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.argument;
import static net.polarizedions.jamesbot.commands.brigadier.TypeFixer.literal;
import static net.polarizedions.jamesbot.utils.IRCColors.BOLD;
import static net.polarizedions.jamesbot.utils.IRCColors.CYAN;
import static net.polarizedions.jamesbot.utils.IRCColors.RESET;

public class Mojira extends Module implements ICommand {
    public Mojira(Bot bot) {
        super(bot);
    }

    @Override
    public void register(CommandDispatcher<CommandMessage> dispatcher) {
        dispatcher.register(
                literal("bug").then(
                        argument("key", string()).executes(c -> this.issue(c.getSource(), getString(c, "key")))
                )
        );
    }

    int issue(CommandMessage source, String key) {
        Pair<MojiraAPI.STATUS, MojiraAPI.MojiraIssue> issueResp = MojiraAPI.getIssue(key);
        MojiraAPI.STATUS returnType = issueResp.getOne();
        MojiraAPI.MojiraIssue issue = issueResp.getTwo();

        if (returnType == MojiraAPI.STATUS.NOT_FOUND) {
            source.respond(key + ": Issue not found");
            return ReturnConstants.FAIL_REPLIED;
        }
        else if (returnType == MojiraAPI.STATUS.NO_PERMISSION) {
            source.respond(key + " is a private issue");
            return ReturnConstants.FAIL_REPLIED;
        }
        else if (returnType == MojiraAPI.STATUS.UNKNOWN) {
            source.respond("Unknown error occurred");
            return ReturnConstants.FAIL_REPLIED;
        }

        String extra = issue.fixVersion != null ? ": " + issue.fixVersion : (issue.duplicate != null ? ": " + issue.duplicate : "");
        String url = String.format("https://bugs.mojang.com/browse/%s", issue.key);
        String reply = String.format("[%s%s%s] %s | %s%s%s%s | %s", CYAN, issue.key, RESET, issue.description, BOLD, issue.state, extra, RESET, url);

        source.respond(reply);

        return ReturnConstants.SUCCESS;
    }

    @Override
    public String getHelp() {
        return "Quickly access issues on the Mojang Bug-tracker";
    }

    @Override
    public String getUsage() {
        return "!bug <BUG ID>";
    }

    @Override
    public String getModuleName() {
        return "mojira";
    }
}
