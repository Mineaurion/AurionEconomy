package com.mineaurion.economy.bukkit;

import com.mineaurion.economy.common.plugin.EconomyBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public class Bootstrap extends JavaPlugin implements EconomyBootstrap {

    private final Economy plugin;

    private final BukkitSchedulerAdapter schedulerAdapter;

    public Bootstrap(){
        this.plugin = new Economy(this);
        this.schedulerAdapter = new BukkitSchedulerAdapter(this);
    }

    @Override
    public void onEnable(){
        plugin.enable();
    }

    @Override
    public void onDisable() {
        plugin.disable();
    }

    @Override
    public BukkitSchedulerAdapter getScheduler() {
        return schedulerAdapter;
    }

    @Override
    public Path getDataDirectory() {
        return getDataFolder().toPath().toAbsolutePath();
    }
}
