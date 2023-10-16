package com.mineaurion.aurioneconomy.forge;

import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

import static com.mineaurion.aurioneconomy.forge.AurionEconomy.toNativeText;

public class SenderFactory extends com.mineaurion.aurioneconomy.common.command.sender.SenderFactory<AurionEconomy, CommandSourceStack> {

    public SenderFactory(AurionEconomy plugin){
        super(plugin);
    }

    @Override
    protected UUID getUniqueId(CommandSourceStack commandSource) {
        if(commandSource.getEntity() instanceof Player){
            return commandSource.getEntity().getUUID();
        }
        return Sender.CONSOLE_UUID;
    }

    @Override
    protected String getName(CommandSourceStack commandSource) {
        if(commandSource.getEntity() instanceof Player){
            return commandSource.getTextName();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    protected void sendMessage(CommandSourceStack sender, Component message) {
        sender.sendSuccess(toNativeText(message), false);
    }

    @Override
    protected boolean hasPermission(CommandSourceStack sender, String permission) {
        // If admin permission we check if the sender is OP
        return permission.contains("admin") ? sender.hasPermission(4) : sender.hasPermission(0);
    }

    @Override
    protected boolean isConsole(CommandSourceStack sender) {
        boolean output = true;
        try {
            ServerPlayer player = sender.getPlayerOrException();
            output = false;
        } catch (CommandSyntaxException e){
            // ignore this is not a player
        }
        return output;

    }
}
