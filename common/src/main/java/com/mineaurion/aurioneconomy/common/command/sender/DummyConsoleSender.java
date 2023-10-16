package com.mineaurion.aurioneconomy.common.command.sender;

import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

import java.util.UUID;

public abstract class DummyConsoleSender implements Sender {
    private final AurionEconomyPlugin platform;

    public DummyConsoleSender(AurionEconomyPlugin plugin){
        this.platform = plugin;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public boolean isConsole() {
        return true;
    }

    @Override
    public AurionEconomyPlugin getPlugin() {
        return this.platform;
    }

    @Override
    public UUID getUUID() {
        return CONSOLE_UUID;
    }

    @Override
    public String getName() {
        return CONSOLE_NAME;
    }
}
