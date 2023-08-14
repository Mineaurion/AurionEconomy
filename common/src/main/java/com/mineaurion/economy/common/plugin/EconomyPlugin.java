package com.mineaurion.economy.common.plugin;

import com.mineaurion.economy.common.logger.PluginLogger;
import com.mineaurion.economy.common.storage.Storage;

public interface EconomyPlugin {

    EconomyBootstrap getBootstrap();

    PluginLogger getLogger();

    Storage getStorage();
}
