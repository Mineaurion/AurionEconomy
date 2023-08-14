package com.mineaurion.economy.bukkit;

import com.mineaurion.economy.common.logger.JavaPluginLogger;
import com.mineaurion.economy.common.logger.PluginLogger;
import com.mineaurion.economy.common.plugin.AbstractEconomyPlugin;
import com.mineaurion.economy.common.plugin.EconomyBootstrap;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class Economy extends AbstractEconomyPlugin {

    public final Bootstrap bootstrap;

    private SenderFactory senderFactory;
    private CommandExecutor commandManager;

    public Economy(Bootstrap bootstrap){
        this.bootstrap = bootstrap;
    }

    @Override
    protected void registerPlatformListeners() {
        // this.bootstrap.getServer().getPluginManager().registerEvent();
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

        //TODO: register command here
    }

    @Override
    protected void setupSenderFactory() {
        this.senderFactory = new SenderFactory(this);
    }

    public SenderFactory getSenderFactory(){
        return this.senderFactory;
    }

    @Override
    public EconomyBootstrap getBootstrap() {
        return this.bootstrap;
    }

    public Server getServer(){
        return bootstrap.getServer();
    }

    public JavaPlugin getLoader(){
        return this.bootstrap;
    }

    @Override
    public PluginLogger getLogger() {
        return new JavaPluginLogger(Bukkit.getLogger());
    }
}
