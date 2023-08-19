package com.mineaurion.aurioneconomy.common.plugin;

import com.mineaurion.aurioneconomy.common.logger.PluginLogger;
import com.mineaurion.aurioneconomy.common.storage.Storage;
import com.mineaurion.aurioneconomy.common.storage.StorageFactory;

import java.util.Optional;
import java.util.UUID;

public abstract class AbstractAurionEconomyPlugin implements AurionEconomyPlugin {

    private Storage storage;

    private final PluginLogger logger;

    public AbstractAurionEconomyPlugin(PluginLogger logger){
        this.logger = logger;
    }

    public final void enable(){
        logger.info("Economy Starting");

        setupSenderFactory();

        StorageFactory storageFactory = new StorageFactory(this);

        // init storage
        this.storage = storageFactory.getInstance();

        // register listeners
        registerPlatformListeners();

        // register commands
        registerCommands();
    }

    public final void disable(){
        // cancel delayed/repeating tasks
        getBootstrap().getScheduler().shutdownScheduler();;

        // close storage
        logger.info("Closing storage...");
        this.storage.shutdown();

        // shutdown async executor pool
        getBootstrap().getScheduler().shutdownExecutor();
    }

    protected abstract void setupSenderFactory();
    protected abstract void registerPlatformListeners();
    protected abstract void registerCommands();

    public abstract Optional<UUID> lookupUUID(String username);
    public abstract Optional<String> lookupUsername(UUID uuid);


    @Override
    public Storage getStorage() {
        return this.storage;
    }
}
