package com.mineaurion.economy.bukkit;

import com.mineaurion.economy.common.command.sender.Sender;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SenderFactory extends com.mineaurion.economy.common.command.sender.SenderFactory<Economy, CommandSender> {

    private final BukkitAudiences audiences;

    public SenderFactory(Economy plugin){
        super(plugin);
        this.audiences = BukkitAudiences.create(plugin.getLoader());
    }

    @Override
    protected String getName(CommandSender sender) {
        return sender instanceof Player ? sender.getName() : Sender.CONSOLE_NAME;
    }

    @Override
    protected UUID getUniqueId(CommandSender sender) {
        return sender instanceof Player ? ((Player) sender).getUniqueId() : Sender.CONSOLE_UUID;
    }

    @Override
    protected void sendMessage(CommandSender sender, Component message) {
        if(sender instanceof Player || sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender){
            this.audiences.sender(sender).sendMessage(message);
        } else {
            getPlugin().getBootstrap().getScheduler().executeSync(() -> this.audiences.sender(sender).sendMessage(message));
        }
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String permission) {
        return sender.hasPermission(permission);
    }

    @Override
    protected void performCommand(CommandSender sender, String command) {
        getPlugin().getServer().dispatchCommand(sender, command);
    }

    @Override
    protected boolean isConsole(CommandSender sender) {
        return sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender;
    }

    @Override
    public void close() {
        super.close();
        this.audiences.close();
    }
}
