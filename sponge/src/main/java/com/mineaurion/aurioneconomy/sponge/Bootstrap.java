package com.mineaurion.aurioneconomy.sponge;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mineaurion.aurioneconomy.common.logger.Log4jPluginLogger;
import com.mineaurion.aurioneconomy.common.logger.PluginLogger;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyBootstrap;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
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

@Plugin("aurioneconomy")
public class Bootstrap implements AurionEconomyBootstrap, Supplier<Injector> {

    private final Injector injector;
    private final SpongeSchedulerAdapter schedulerAdapter;
    private AurionEconomy plugin;

    private final Game game;
    private final PluginContainer pluginContainer;

    @Inject
    public Bootstrap(Injector injector){
        this.injector = injector;
        this.game = injector.getInstance(Game.class);
        this.pluginContainer = injector.getInstance(PluginContainer.class);
        // injector.injectMembers(this);

        this.schedulerAdapter = new SpongeSchedulerAdapter(this.game, this.pluginContainer);
        System.out.println("Je paaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaasse laa");
    }

    @Override
    public Injector get() {
        return this.injector;
    }

    @Listener(order = Order.FIRST)
    public void onEnable(ConstructPluginEvent event){
        System.out.println("Je passe la 222222222222222");
        this.plugin = new AurionEconomy(this);
        this.plugin.enable();
    }

    @Listener
    public void onDisable(StoppingEngineEvent<Server> event){
        this.plugin.disable();
    }

    public Game getGame() {
        return game;
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
    public Path getDataDirectory() {
        Path dataDirectory = this.game.gameDirectory().toAbsolutePath().resolve("aurioneconomy");
        try {
            createDirectoriesIfNotExists(dataDirectory);
        } catch (IOException e){
            getLogger().warn("Unable to create AurionEconomy directory", e);
        }
        return dataDirectory;
    }

    public Optional<Server> getServer() {
        return this.game.isServerAvailable() ? Optional.of(this.game.server()) : Optional.empty();
    }

    public static Path createDirectoriesIfNotExists(Path path) throws IOException {
        if (Files.exists(path) && (Files.isDirectory(path) || Files.isSymbolicLink(path))) {
            return path;
        }

        try {
            Files.createDirectories(path);
        } catch (FileAlreadyExistsException e) {
            // ignore
        }

        return path;
    }

    public PluginLogger getLogger() {
        return new Log4jPluginLogger(LogManager.getLogger("AurionEconomy"));
    }
}
