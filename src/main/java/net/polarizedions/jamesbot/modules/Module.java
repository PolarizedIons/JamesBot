package net.polarizedions.jamesbot.modules;

public abstract class Module {
    protected boolean active = this.isActiveByDefault();

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
