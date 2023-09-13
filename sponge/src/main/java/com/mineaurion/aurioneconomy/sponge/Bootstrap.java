package com.mineaurion.aurioneconomy.sponge;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mineaurion.aurioneconomy.common.logger.Log4jPluginLogger;
import com.mineaurion.aurioneconomy.common.logger.PluginLogger;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyBootstrap;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.lifecycle.ConstructPluginEvent;
import org.spongepowered.api.event.lifecycle.StoppingEngineEvent;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

@Plugin(AurionEconomyPlugin.MOD_ID)
public class Bootstrap implements AurionEconomyBootstrap, Supplier<Injector> {

    private final Injector injector;
    private final SpongeSchedulerAdapter schedulerAdapter;
    private AurionEconomy plugin;

    private final Game game;
    private final PluginContainer pluginContainer;

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDirectory;

    @Inject
    public Bootstrap(Injector injector){
        this.injector = injector;
        this.game = injector.getInstance(Game.class);
        this.pluginContainer = injector.getInstance(PluginContainer.class);
        this.schedulerAdapter = new SpongeSchedulerAdapter(this.game, this.pluginContainer);
    }

    @Override
    public Injector get() {
        return this.injector;
    }

    @Listener(order = Order.FIRST)
    public void onEnable(ConstructPluginEvent event){
        this.plugin = new AurionEconomy(this);
        this.plugin.enable();
    }

    @Listener
    public void onDisable(StoppingEngineEvent<Server> event){
        this.plugin.disable();
    }

    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }

    public void registerListeners(Object obj){
        this.game.eventManager().registerListeners(this.pluginContainer, obj);
    }

    @Override
    public SpongeSchedulerAdapter getScheduler() {
        return this.schedulerAdapter;
    }

    @Override
    public Path getConfigDirectory() {
        return configDirectory;
    }

    public Optional<Server> getServer() {
        return this.game.isServerAvailable() ? Optional.of(this.game.server()) : Optional.empty();
    }

    public static void createDirectoriesIfNotExists(Path path) throws IOException {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return;
        }

        try {
            Files.createDirectories(path);
        } catch (FileAlreadyExistsException e) {
            // ignore
        }
    }

    public PluginLogger getLogger() {
        return new Log4jPluginLogger(LogManager.getLogger(AurionEconomyPlugin.NAME));
    }
}
