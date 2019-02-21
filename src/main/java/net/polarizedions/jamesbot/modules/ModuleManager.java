package net.polarizedions.jamesbot.modules;

import net.polarizedions.jamesbot.config.BotConfig;
import net.polarizedions.jamesbot.core.Bot;
import net.polarizedions.jamesbot.modules.chat.ActionResponses;
import net.polarizedions.jamesbot.modules.chat.FetchTitle;
import net.polarizedions.jamesbot.modules.chat.MinecraftWiki;
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

    public ModuleManager() {
        this.modules = new ArrayList<>();

        // Chat
        this.add(new ActionResponses());
        this.add(new FetchTitle());
        this.add(new MinecraftWiki());
        this.add(new Qmark());
        this.add(new Quotes());
        this.add(new Say());
        this.add(new Temperature());
        this.add(new Time());

        // Core
        this.add(new About());
        this.add(new JoinPart());
        this.add(new Ping());

        // Fun
        this.add(new ButtcoinCollector());
        this.add(new ButtcoinCommand());
        this.add(new ButtcoinPlusPlus());
        this.add(new Dice());
        this.add(new Eightball());
        this.add(new Memes());
        this.add(new WhatIsLove());


        // Internet
        this.add(new Steam());
        this.add(new Twitter());
        this.add(new Youtube());
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
        Bot.instance.saveBotConfig();
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
