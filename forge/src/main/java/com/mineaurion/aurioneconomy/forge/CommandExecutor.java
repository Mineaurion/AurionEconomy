package com.mineaurion.aurioneconomy.forge;

import com.mineaurion.aurioneconomy.common.command.BrigadierCommandManager;
import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommandExecutor extends BrigadierCommandManager<CommandSource> {

    private final AurionEconomy plugin;

    public CommandExecutor(AurionEconomy plugin){
        super(plugin);
        this.plugin = plugin;
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event){
        for(String alias: COMMAND_ALIASES){
            LiteralCommandNode<CommandSource> command = Commands.literal(alias).executes(this).build();
            ArgumentCommandNode<CommandSource, String> arguments = Commands.argument("args", StringArgumentType.greedyString())
                    .suggests(this)
                    .executes(this)
                    .build();
            command.addChild(arguments);
            event.getDispatcher().getRoot().addChild(command);
        }
    }

    @Override
    public Sender getSender(CommandSource source) {
        return this.plugin.getSenderFactory().wrap(source);
    }

}
