package com.mineaurion.aurioneconomy.fabric;

import com.mineaurion.aurioneconomy.common.command.BrigadierCommandManager;
import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class CommandExecutor extends BrigadierCommandManager<ServerCommandSource> {

    private final AurionEconomy plugin;

    public CommandExecutor(AurionEconomy plugin){
        super(plugin);
        this.plugin = plugin;
    }

    public void register(){
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            for(String alias: COMMAND_ALIASES){
                LiteralCommandNode<ServerCommandSource> command = literal(alias).executes(this).build();

                ArgumentCommandNode<ServerCommandSource, String> arguments = argument("args", greedyString())
                        .suggests(this)
                        .executes(this)
                        .build();
                command.addChild(arguments);
                dispatcher.getRoot().addChild(command);
            }
        });
    }

    @Override
    public Sender getSender(ServerCommandSource source) {
        return this.plugin.getSenderFactory().wrap(source);
    }
}
