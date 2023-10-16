package com.mineaurion.aurioneconomy.fabric;

import com.mineaurion.aurioneconomy.common.config.ConfigurationAdapter;
import com.mineaurion.aurioneconomy.common.logger.Log4jPluginLogger;
import com.mineaurion.aurioneconomy.common.plugin.AbstractAurionEconomyPlugin;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import com.mojang.authlib.GameProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new SenderFactory(this);
    }

    @Override
    protected void registerPlatformListeners() {
        // Too late for fabric registered in #registerListeners
    }

    @Override
    protected void registerCommands() {
        // Too late for fabric registered in #registerListeners
    }

    protected void registerListeners(){
        this.commandManager = new CommandExecutor(this);
        this.commandManager.register();
    }

    @Override
    public Optional<UUID> lookupUUID(String username) {
        return bootstrap.getServer().map(MinecraftServer::getUserCache).flatMap(c -> c.findByName(username)).map(GameProfile::getId);
    }

    @Override
    public void sendMessageToSpecificPlayer(UUID uuid, Component message) {
        Optional<ServerPlayerEntity> player = bootstrap.getServer().map(MinecraftServer::getPlayerManager).map(s -> s.getPlayer(uuid));
        player.ifPresent(p -> p.sendMessage(toNativeText(message)));
    }

    @Override
    public ConfigurationAdapter getConfigurationAdapter() {
        return new FabricConfigAdapter(this, resolveConfig(AurionEconomyPlugin.MOD_ID + ".conf"));
    }

    @Override
    public Collection<String> getPlayersList() {
        return bootstrap.getServer()
                .map(MinecraftServer::getPlayerManager)
                .map(PlayerManager::getPlayerList)
                .map(serverPlayer -> serverPlayer.stream().map(p -> p.getGameProfile().getName()).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public static Text toNativeText(Component component) {
        return Text.Serializer.fromJson(GsonComponentSerializer.gson().serialize(component));
    }

    public SenderFactory getSenderFactory() {
        return senderFactory;
    }
}
