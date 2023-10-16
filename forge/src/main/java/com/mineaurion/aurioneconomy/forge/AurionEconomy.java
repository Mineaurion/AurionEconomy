package com.mineaurion.aurioneconomy.forge;

import com.mineaurion.aurioneconomy.common.config.ConfigurationAdapter;
import com.mineaurion.aurioneconomy.common.logger.Log4jPluginLogger;
import com.mineaurion.aurioneconomy.common.plugin.AbstractAurionEconomyPlugin;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import com.mojang.authlib.GameProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.apache.logging.log4j.LogManager;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AurionEconomy extends AbstractAurionEconomyPlugin {

    public final Bootstrap bootstrap;

    private SenderFactory senderFactory;

    private CommandExecutor commandManager;

    public AurionEconomy(Bootstrap bootstrap){
        super(new Log4jPluginLogger(LogManager.getLogger(AurionEconomyPlugin.NAME)));
        this.bootstrap = bootstrap;
    }

    @Override
    public Bootstrap getBootstrap() {
        return bootstrap;
    }

    protected void registerEarlyListeners(){
        this.commandManager = new CommandExecutor(this);
        this.bootstrap.registerListeners(this.commandManager);
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new SenderFactory(this);
    }

    public SenderFactory getSenderFactory() {
        return senderFactory;
    }

    @Override
    protected void registerPlatformListeners() {
        // Not used for forge, registered in #registerEarlyListeners
    }

    @Override
    protected void registerCommands() {
        // Not used for forge, registered in #registerEarlyListeners
    }

    @Override
    public ConfigurationAdapter getConfigurationAdapter() {
        return new ForgeConfigAdapter(this, resolveConfig(AurionEconomyPlugin.MOD_ID + ".conf"));
    }

    @Override
    public Optional<UUID> lookupUUID(String username) {
        return bootstrap.getServer().map(MinecraftServer::getProfileCache).flatMap(profileCache -> profileCache.get(username)).map(GameProfile::getId);
    }

    @Override
    public Collection<String> getPlayersList() {
        return bootstrap.getServer()
                .map(MinecraftServer::getPlayerList)
                .map(PlayerList::getPlayers)
                .map(serverPlayers -> serverPlayers.stream().map(p -> p.getGameProfile().getName()).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    @Override
    public void sendMessageToSpecificPlayer(UUID uuid, Component message) {
        Optional<ServerPlayer> player = getBootstrap().getServer().map(MinecraftServer::getPlayerList).map(playerList -> playerList.getPlayer(uuid));
        player.ifPresent(p -> p.sendMessage(toNativeText(message), Util.NIL_UUID));
    }

    public static net.minecraft.network.chat.Component toNativeText(Component component){
        return net.minecraft.network.chat.Component.Serializer.fromJson(GsonComponentSerializer.gson().serialize(component));
    }
}
