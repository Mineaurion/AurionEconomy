package com.mineaurion.aurioneconomy.forge;

import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.UUID;

import static com.mineaurion.aurioneconomy.forge.AurionEconomy.toNativeText;

public class SenderFactory extends com.mineaurion.aurioneconomy.common.command.sender.SenderFactory<AurionEconomy, CommandSource> {

    public SenderFactory(AurionEconomy plugin){
        super(plugin);
    }

    @Override
    protected UUID getUniqueId(CommandSource commandSource) {
        if(commandSource.getEntity() instanceof PlayerEntity){
            return commandSource.getEntity().getUUID();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    protected String getName(CommandSource commandSource) {
        if(commandSource.getEntity() instanceof PlayerEntity){
            return commandSource.getTextName();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    protected void sendMessage(CommandSource sender, Component message) {
        sender.sendSuccess(toNativeText(message), false);
    }

    @Override
    protected boolean hasPermission(CommandSource sender, String permission) {
        // If admin permission we check if the sender is OP
        return permission.contains("admin") ? sender.hasPermission(4) : sender.hasPermission(0);
    }

    @Override
    protected boolean isConsole(CommandSource sender) {
        boolean output = true;
        try {
            ServerPlayerEntity player = sender.getPlayerOrException();
            output = false;
        } catch (CommandSyntaxException e){
            // ignore this is not a player
        }
        return output;
    }


}
