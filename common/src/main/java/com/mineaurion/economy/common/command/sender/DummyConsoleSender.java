package com.mineaurion.economy.common.command.sender;

import com.mineaurion.economy.common.plugin.EconomyPlugin;

import java.util.UUID;

public abstract class DummyConsoleSender implements Sender {
    private final EconomyPlugin platform;

    public DummyConsoleSender(EconomyPlugin plugin){
        this.platform = plugin;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void performCommand(String commandLine) {}

    @Override
    public boolean isConsole() {
        return true;
    }

    @Override
    public EconomyPlugin getPlugin() {
        return this.platform;
    }

    @Override
    public UUID getUUID() {
        return Sender.CONSOLE_UUID;
    }

    @Override
    public String getName() {
        return Sender.CONSOLE_NAME;
    }
}
