package com.mineaurion.aurioneconomy.common.plugin;

import com.mineaurion.aurioneconomy.common.plugin.scheduler.SchedulerAdapter;

import java.io.InputStream;
import java.nio.file.Path;

public interface AurionEconomyBootstrap {

    SchedulerAdapter getScheduler();

    /**
     * Gets the plugins main data storage directory
     *
     * <p>Bukkit: ./plugins/Economy</p>
     * <p>Sponge: ./Economy/</p>
     * <p>Fabric: ./mods/Economy</p>
     * <p>Forge: ./config/Economy</p>
     *
     * @return the platforms data folder
     */
    Path getDataDirectory();

    default Path getConfigDirectory(){
        return getDataDirectory();
    }

    default InputStream getResourceStream(String path){
        return getClass().getClassLoader().getResourceAsStream(path);
    }
}
