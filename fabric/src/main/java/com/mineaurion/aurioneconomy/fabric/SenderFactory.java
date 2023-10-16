package com.mineaurion.aurioneconomy.fabric;

import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mineaurion.aurioneconomy.fabric.mixin.ServerCommandSourceAccessor;
import net.kyori.adventure.text.Component;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.rcon.RconCommandOutput;

import java.util.UUID;

import static com.mineaurion.aurioneconomy.fabric.AurionEconomy.toNativeText;

public class SenderFactory extends com.mineaurion.aurioneconomy.common.command.sender.SenderFactory<AurionEconomy, ServerCommandSource> {

    public SenderFactory(AurionEconomy plugin){
        super(plugin);
    }

    @Override
    protected UUID getUniqueId(ServerCommandSource sender) {
        if(sender.getEntity() != null){
            return sender.getEntity().getUuid();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    protected String getName(ServerCommandSource sender) {
        String name = sender.getName();
        if(sender.getEntity() != null && name.equals("Server")){
            return Sender.CONSOLE_NAME;
        }
        return name;
    }

    @Override
    protected void sendMessage(ServerCommandSource sender, Component message) {
        sender.sendFeedback(toNativeText(message), false);
    }

    @Override
    protected boolean hasPermission(ServerCommandSource sender, String permission) {
        return permission.contains("admin") ? sender.hasPermissionLevel(4) : sender.hasPermissionLevel(0);
    }

    @Override
    protected boolean isConsole(ServerCommandSource sender) {
        CommandOutput output = ((ServerCommandSourceAccessor) sender).getOutput();
        return output == sender.getMinecraftServer() || // console
            output.getClass() == RconCommandOutput.class || // Rcon
            (output == CommandOutput.DUMMY && sender.getName().equals("")); // Functions
    }
}
