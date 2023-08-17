package com.mineaurion.economy.common.plugin;

import com.mineaurion.economy.common.logger.PluginLogger;
import com.mineaurion.economy.common.storage.Storage;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;

import java.util.Optional;
import java.util.UUID;

public interface EconomyPlugin {

    EconomyBootstrap getBootstrap();

    PluginLogger getLogger();

    Storage getStorage();

    AudienceProvider getAudiences();

    Optional<UUID> lookupUUID(String username);
    Optional<String> lookupUsername(UUID uuid);
    void sendMessageToSpecificPlayer(UUID uuid, Component message);
}
