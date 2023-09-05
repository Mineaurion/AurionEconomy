package com.mineaurion.aurioneconomy.common.plugin;

import com.mineaurion.aurioneconomy.common.logger.PluginLogger;
import com.mineaurion.aurioneconomy.common.storage.Storage;
import com.mineaurion.aurioneconomy.common.storage.StorageFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractAurionEconomyPlugin implements AurionEconomyPlugin {
    private Storage storage;

    private final PluginLogger logger;

    public AbstractAurionEconomyPlugin(PluginLogger logger){
        this.logger = logger;
    }

    public final void enable(){
        logger.info(AurionEconomyPlugin.NAME + " starting");

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

    protected Path resolveConfig(String filename){
        Path configFile = getBootstrap().getConfigDirectory().resolve(filename);

        // if the cfg don't exist create it from resource dir
        if(!Files.exists(configFile)){
            try {
                Files.createDirectories(configFile.getParent());
            } catch (IOException e){
                // ignore
            }

            try(InputStream is = getBootstrap().getResourceStream(filename)){
                Files.copy(is, configFile);
            } catch (IOException e){
                throw new RuntimeException(e);
            }
        }
        return configFile;
    }

    protected abstract void setupSenderFactory();
    protected abstract void registerPlatformListeners();
    protected abstract void registerCommands();

    public abstract Optional<UUID> lookupUUID(String username);

    @Override
    public PluginLogger getLogger() {
        return this.logger;
    }

    @Override
    public Storage getStorage() {
        return this.storage;
    }
}
