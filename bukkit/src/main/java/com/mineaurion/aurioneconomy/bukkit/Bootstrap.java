package com.mineaurion.aurioneconomy.bukkit;

import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

public class Bootstrap extends JavaPlugin implements AurionEconomyBootstrap {

    private final AurionEconomy plugin;

    private final BukkitSchedulerAdapter schedulerAdapter;

    public Bootstrap(){
        this.plugin = new AurionEconomy(this);
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
