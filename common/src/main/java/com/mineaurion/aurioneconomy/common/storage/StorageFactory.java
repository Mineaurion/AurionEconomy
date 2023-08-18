package com.mineaurion.aurioneconomy.common.storage;

import com.mineaurion.aurioneconomy.common.misc.StorageCredentials;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import com.mineaurion.aurioneconomy.common.storage.database.SqlStorage;
import com.mineaurion.aurioneconomy.common.storage.database.StorageImplementation;
import com.mineaurion.aurioneconomy.common.storage.database.hikari.MysqlConnectionFactory;

import java.util.HashMap;

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
                return new SqlStorage(
                        this.plugin,
                        // TODO: Need to be get from configuration file
                        new MysqlConnectionFactory(
                                new StorageCredentials(
                                        "192.168.1.95",
                                        "economy",
                                        "mineaurion",
                                        "password",
                                        2,
                                        1,
                                        60,
                                        60,
                                        600,
                                        new HashMap<>()
                                )
                        ),
                        "aurion_"
                );
            default:
                throw new RuntimeException("Unknown method or not implemented : " + method);
        }
    }
}
