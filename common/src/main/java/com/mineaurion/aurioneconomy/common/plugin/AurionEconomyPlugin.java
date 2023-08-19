package com.mineaurion.aurioneconomy.common.plugin;

import com.mineaurion.aurioneconomy.common.logger.PluginLogger;
import com.mineaurion.aurioneconomy.common.storage.Storage;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.UUID;

public interface AurionEconomyPlugin {

    AurionEconomyBootstrap getBootstrap();

    PluginLogger getLogger();

    Storage getStorage();

    Optional<UUID> lookupUUID(String username);
    Optional<String> lookupUsername(UUID uuid);
    void sendMessageToSpecificPlayer(UUID uuid, Component message);
}
