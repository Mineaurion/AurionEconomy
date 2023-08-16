package com.mineaurion.economy.common.plugin;

import com.mineaurion.economy.common.logger.PluginLogger;
import com.mineaurion.economy.common.storage.Storage;

import java.util.Optional;
import java.util.UUID;

public interface EconomyPlugin {

    EconomyBootstrap getBootstrap();

    PluginLogger getLogger();

    Storage getStorage();

    Optional<UUID> lookupUUID(String username);
    Optional<String> lookupUsername(UUID uuid);
}
