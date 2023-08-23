package com.mineaurion.aurioneconomy.bukkit;

import com.mineaurion.aurioneconomy.bukkit.vault.VaultConnector;
import com.mineaurion.aurioneconomy.common.logger.JavaPluginLogger;
import com.mineaurion.aurioneconomy.common.plugin.AbstractAurionEconomyPlugin;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;

import java.util.Optional;
import java.util.UUID;

public class AurionEconomy extends AbstractAurionEconomyPlugin {

    public final Bootstrap bootstrap;

    private SenderFactory senderFactory;
    private CommandExecutor commandManager;

    private BukkitAudiences audiences;

    public AurionEconomy(Bootstrap bootstrap){
        super(new JavaPluginLogger(Bukkit.getLogger()));
        this.bootstrap = bootstrap;
    }

    @Override
    protected void registerPlatformListeners() {
        // this.bootstrap.getServer().getPluginManager().registerEvent();
        if(!setupEconomy()){
            // TODO: handle it better, this will cause an error when the command will be register
            getLogger().severe("Disabled due to no Vault Dependency found");
            getServer().getPluginManager().disablePlugin(this.bootstrap);
        }
    }

    private boolean setupEconomy(){
        if(getServer().getPluginManager().getPlugin("Vault") == null){
            getLogger().severe("Vault not found");
            return false;
        }
        getServer().getServicesManager().register(net.milkbowl.vault.economy.Economy.class, new VaultConnector(this), this.bootstrap, ServicePriority.Highest);

        return true;
    }

    @Override
    protected void registerCommands() {
        PluginCommand command = this.bootstrap.getCommand("money");
        if(command == null){
            getLogger().severe("Unable to register /economy command with the server");
            return;
        }

        this.commandManager = new CommandExecutor(this, command);
        this.commandManager.register();
    }

    @Override
    protected void setupSenderFactory() {
        this.audiences = BukkitAudiences.create(getBootstrap());
        this.senderFactory = new SenderFactory(this);
    }

    @Override
    public Optional<UUID> lookupUUID(String username) {
        return Optional.ofNullable(getServer().getOfflinePlayer(username)).map(OfflinePlayer::getUniqueId);
    }

    @Override
    public Optional<String> lookupUsername(UUID uuid) {
        return Optional.ofNullable(getServer().getOfflinePlayer(uuid)).map(OfflinePlayer::getName);
    }

    public void sendMessageToSpecificPlayer(UUID uuid, Component message){
        Player player = getServer().getPlayer(uuid);
        if(player != null){
            audiences.sender(player).sendMessage(message);
        }
    }

    public BukkitAudiences getAudiences(){
        return this.audiences;
    }

    public SenderFactory getSenderFactory(){
        return this.senderFactory;
    }

    @Override
    public Bootstrap getBootstrap() {
        return this.bootstrap;
    }

    public Server getServer(){
        return bootstrap.getServer();
    }
}
