package com.mineaurion.economy.bukkit;

import com.mineaurion.economy.common.command.CommandManager;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;


public class CommandExecutor extends CommandManager implements org.bukkit.command.CommandExecutor, Listener {

    private static final boolean SELECT_ENTITIES_SUPPORTED;

    static {
        boolean selectEntitesSupported = false;
        try {
            Server.class.getMethod("selectEntities", CommandSender.class, String.class);
            selectEntitesSupported = true;
        } catch (NoSuchMethodException e){
            // ignore
        }
        SELECT_ENTITIES_SUPPORTED = selectEntitesSupported;
    }

    protected final Economy plugin;
    protected final PluginCommand command;

    public CommandExecutor(Economy plugin, PluginCommand command){
        super(plugin);
        this.plugin = plugin;
        this.command = command;
    }

    public void register(){
        this.command.setExecutor(this);
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin.bootstrap);
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        executeCommand(
                this.plugin.getSenderFactory().wrap(sender),
                label,
                Arrays.asList(args)
        );
        return true;
    }

    // Support Economy command prefixed with a '/' from the console
    @EventHandler(ignoreCancelled = true)
    public void onConsoleCommand(ServerCommandEvent e){
        if(!(e.getSender() instanceof ConsoleCommandSender)){
            return;
        }

        String buffer = e.getCommand();
        if(buffer.isEmpty() || buffer.charAt(0) != '/'){
            return;
        }

        buffer = buffer.substring(1);

        String commandLabel;
        int firstSpace = buffer.indexOf(' ');
        commandLabel = firstSpace == -1 ? buffer : buffer.substring(0, firstSpace);
        Command command = this.plugin.getServer().getCommandMap().getCommand(commandLabel);
        if(command != this.command){
            return;
        }

        e.setCommand(buffer);
    }

}
