package net.polarizedions.jamesbot.modules;

import net.polarizedions.jamesbot.config.BotConfig;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.chat.ActionResponses;
import net.polarizedions.jamesbot.modules.chat.FetchTitle;
import net.polarizedions.jamesbot.modules.chat.MinecraftWiki;
import net.polarizedions.jamesbot.modules.chat.Mojira;
import net.polarizedions.jamesbot.modules.chat.Qmark;
import net.polarizedions.jamesbot.modules.chat.Quotes;
import net.polarizedions.jamesbot.modules.chat.Say;
import net.polarizedions.jamesbot.modules.chat.Temperature;
import net.polarizedions.jamesbot.modules.chat.Time;
import net.polarizedions.jamesbot.modules.core.About;
import net.polarizedions.jamesbot.modules.core.JoinPart;
import net.polarizedions.jamesbot.modules.core.Ping;
import net.polarizedions.jamesbot.modules.fun.ButtcoinCollector;
import net.polarizedions.jamesbot.modules.fun.ButtcoinCommand;
import net.polarizedions.jamesbot.modules.fun.ButtcoinPlusPlus;
import net.polarizedions.jamesbot.modules.fun.Dice;
import net.polarizedions.jamesbot.modules.fun.Eightball;
import net.polarizedions.jamesbot.modules.fun.Memes;
import net.polarizedions.jamesbot.modules.fun.WhatIsLove;
import net.polarizedions.jamesbot.modules.internet.Steam;
import net.polarizedions.jamesbot.modules.internet.Twitter;
import net.polarizedions.jamesbot.modules.internet.Youtube;
import net.polarizedions.jamesbot.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {
    List<Pair<String, Module>> modules; // Can't be a map because multiple entries could have the same name
    Bot bot;

    public ModuleManager(Bot bot) {
        this.bot = bot;
        this.modules = new ArrayList<>();

        // Chat
        this.add(new ActionResponses(bot));
        this.add(new FetchTitle(bot));
        this.add(new MinecraftWiki(bot));
        this.add(new Mojira(bot));
        this.add(new Qmark(bot));
        this.add(new Quotes(bot));
        this.add(new Say(bot));
        this.add(new Temperature(bot));
        this.add(new Time(bot));

        // Core
        this.add(new About(bot));
        this.add(new JoinPart(bot));
        this.add(new Ping(bot));

        // Fun
        this.add(new ButtcoinCollector(bot));
        this.add(new ButtcoinCommand(bot));
        this.add(new ButtcoinPlusPlus(bot));
        this.add(new Dice(bot));
        this.add(new Eightball(bot));
        this.add(new Memes(bot));
        this.add(new WhatIsLove(bot));


        // Internet
        this.add(new Steam(bot));
        this.add(new Twitter(bot));
        this.add(new Youtube(bot));
    }

    private void add(Module module) {
        this.modules.add(new Pair<>(module.getModuleName(), module));
    }

    public Map<String, Boolean> getState() {
        Map<String, Boolean> result = new HashMap<>();
        for (Pair<String, Module> entry : this.modules) {
            result.put(entry.getOne(), entry.getTwo().isActive());
        }

        return result;
    }

    public void initConfig(BotConfig botConfig) {
        for (Pair<String, Module> mod : this.modules) {
            if (botConfig.enabledModules.containsKey(mod.getTwo().getModuleName())) {
                mod.getTwo().setActive(botConfig.enabledModules.get(mod.getOne()));
            }
        }

        botConfig.enabledModules = this.getState();
        this.bot.saveBotConfig();
    }

    public List<Module> getModules(Class filter) {
        List<Module> result = new ArrayList<>();
        for (Pair<String, Module> modPair : this.modules) {
            if (filter.isInstance(modPair.getTwo())) {
                result.add(modPair.getTwo());
            }
        }

        return result;
    }

    public boolean isEnabled(String name) {
        for (Pair<String, Module> modPair : this.modules) {
            if (modPair.getOne().equals(name)) {
                return modPair.getTwo().isActive();
            }
        }

        return false;
    }

    public int getModuleCount() {
        return this.modules.size();
    }
}
