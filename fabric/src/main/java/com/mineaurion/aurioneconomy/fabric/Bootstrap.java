package com.mineaurion.aurioneconomy.fabric;

import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyBootstrap;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import com.mineaurion.aurioneconomy.common.plugin.scheduler.SchedulerAdapter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.MinecraftServer;

import java.nio.file.Path;
import java.util.Optional;

public class Bootstrap implements AurionEconomyBootstrap, DedicatedServerModInitializer {

    private final ModContainer loader;

    private AurionEconomy plugin;

    private FabricSchedulerAdapter schedulerAdapter;

    private MinecraftServer server;

    public Bootstrap(){
        this.loader = FabricLoader.getInstance().getModContainer(AurionEconomyPlugin.MOD_ID).orElseThrow(() -> new RuntimeException("Could not get the " + AurionEconomyPlugin.MOD_ID + " mod container"));
        this.schedulerAdapter = new FabricSchedulerAdapter(this);
    }

    @Override
    public void onInitializeServer(){
        this.plugin = new AurionEconomy(this);
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStopping);
        this.plugin.registerListeners();
    }

    @Override
    public SchedulerAdapter getScheduler() {
        return this.schedulerAdapter;
    }

    @Override
    public Path getDataDirectory() {
        return FabricLoader.getInstance().getGameDir().resolve("mods").resolve(AurionEconomyPlugin.MOD_ID);
    }


    public Optional<MinecraftServer> getServer() {
        return Optional.ofNullable(server);
    }

    private void onServerStarting(MinecraftServer server){
        this.server = server;
        this.plugin.enable();
    }

    private void onServerStopping(MinecraftServer server){
        this.plugin.disable();
        this.server = null;
    }
}
