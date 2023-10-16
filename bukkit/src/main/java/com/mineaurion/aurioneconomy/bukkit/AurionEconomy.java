package com.mineaurion.aurioneconomy.bukkit;

import com.mineaurion.aurioneconomy.bukkit.vault.VaultConnector;
import com.mineaurion.aurioneconomy.common.config.ConfigurationAdapter;
import com.mineaurion.aurioneconomy.common.logger.JavaPluginLogger;
import com.mineaurion.aurioneconomy.common.plugin.AbstractAurionEconomyPlugin;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        // TODO: handle it better, this will cause an error when the command will be register
        if(getServer().getPluginManager().getPlugin("Vault") == null){
            getLogger().severe("Vault not found");
            return;
        }
        getServer().getServicesManager().register(Economy.class, new VaultConnector(this), this.bootstrap, ServicePriority.Normal);

        // TODO: maybe need a settings for enabling this
        Collection<RegisteredServiceProvider<Economy>> econs = Bukkit.getPluginManager().getPlugin("Vault").getServer().getServicesManager().getRegistrations(Economy.class);
        for (RegisteredServiceProvider<Economy> econ : econs) {
            if (econ.getProvider().getName().equalsIgnoreCase("Essentials Economy")||
                    econ.getProvider().getName().equalsIgnoreCase("EssentialsX Economy")) {
                getLogger().info("Disabling Essentials - EssentialsX Economy");
                getServer().getServicesManager().unregister(econ.getProvider());
            }
        }
    }

    @Override
    protected void registerCommands() {
        PluginCommand command = this.bootstrap.getCommand("money");
        if(command == null){
            getLogger().severe("Unable to register /money command with the server");
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
    public Collection<String> getPlayersList() {
        return getServer().getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
    }

    @Override
    public ConfigurationAdapter getConfigurationAdapter() {
        return new BukkitConfigurationAdapter(this, resolveConfig("config.yml").toFile());
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
