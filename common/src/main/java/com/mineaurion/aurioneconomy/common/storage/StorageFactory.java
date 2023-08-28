package com.mineaurion.aurioneconomy.common.storage;

import com.google.common.collect.ImmutableMap;
import com.mineaurion.aurioneconomy.common.config.ConfigurationAdapter;
import com.mineaurion.aurioneconomy.common.misc.StorageCredentials;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import com.mineaurion.aurioneconomy.common.storage.database.SqlStorage;
import com.mineaurion.aurioneconomy.common.storage.database.StorageImplementation;
import com.mineaurion.aurioneconomy.common.storage.database.hikari.MysqlConnectionFactory;

import java.util.Map;


public class StorageFactory {

    private final AurionEconomyPlugin plugin;

    public StorageFactory(AurionEconomyPlugin plugin){
        this.plugin = plugin;
    }

    public Storage getInstance(){
        Storage storage;
        // get storage type here
        StorageType type = StorageType.MYSQL;
        storage = new Storage(this.plugin, createNewImplementation(type));
        storage.init();
        return storage;
    }

    private StorageImplementation createNewImplementation(StorageType method){
        switch (method) {
            case MYSQL:
                return new SqlStorage(this.plugin, new MysqlConnectionFactory(getStorageCredentials()), getTablePrefix());
            default:
                throw new RuntimeException("Unknown method or not implemented : " + method);
        }
    }

    private StorageCredentials getStorageCredentials(){
        ConfigurationAdapter cfg = this.plugin.getConfigurationAdapter();
        int maxPoolSize = cfg.getInteger("data.pool-settings.pool-size", 2);
        int minIdle = cfg.getInteger("data.pool-settings.min-idle", 1);
        int maxLifeTime = cfg.getInteger("data.pool-settings.maximum-lifetime", 1800000);
        int keepAliveTime = cfg.getInteger("data.pool-settings.keep-alive-time", 0);
        int connectionTimeout = cfg.getInteger("data.pool-settings.connection-timeout", 600);
        Map<String, String> props = ImmutableMap.copyOf(cfg.getStringMap("data.pool-settings.properties", ImmutableMap.of()));
        System.out.println("StorageFactory");
        System.out.println(cfg.getString("data.address", "test"));
        return new StorageCredentials(
                cfg.getString("data.address", "localhost"),
                cfg.getString("data.database", "aurioneconomy"),
                cfg.getString("data.username", "root"),
                cfg.getString("data.password", "root"),
                maxPoolSize, minIdle, maxLifeTime, keepAliveTime, connectionTimeout, props
        );
    }

    private String getTablePrefix(){
        return this.plugin.getConfigurationAdapter().getString("data.table-prefix", "aurion_");
    }
}
