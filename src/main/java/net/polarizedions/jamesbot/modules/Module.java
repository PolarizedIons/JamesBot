package net.polarizedions.jamesbot.modules;

import net.polarizedions.jamesbot.core.Bot;

public abstract class Module {
    protected boolean active = this.isActiveByDefault();
    protected Bot bot;

    public Module(Bot bot) {
        this.bot = bot;
    }

    public abstract String getModuleName();

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActiveByDefault() {
        return true;
    }
}
