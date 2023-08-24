package com.mineaurion.aurioneconomy.forge;

import com.mineaurion.aurioneconomy.common.command.BrigadierCommandManager;
import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;
import java.util.ListIterator;

public class CommandExecutor extends BrigadierCommandManager<CommandSourceStack> {

    private final AurionEconomy plugin;

    public CommandExecutor(AurionEconomy plugin){
        super(plugin);
        this.plugin = plugin;
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event){
        for(String alias: COMMAND_ALIASES){
            LiteralCommandNode<CommandSourceStack> command = Commands.literal(alias).executes(this).build();
            ArgumentCommandNode<CommandSourceStack, String> argument = Commands.argument("args", StringArgumentType.greedyString())
                    .suggests(this)
                    .executes(this)
                    .build();
            command.addChild(argument);
            event.getDispatcher().getRoot().addChild(command);
        }
    }

    @Override
    public Sender getSender(CommandSourceStack source) {
        return this.plugin.getSenderFactory().wrap(source);
    }

    @Override
    public List<String> resolveSelector(CommandSourceStack source, List<String> args) {
        // usage of @ selectors requires at least level 2 permission
        CommandSourceStack atAllowedSource = source.hasPermission(2) ? source : source.withPermission(2);
        for (ListIterator<String> it = args.listIterator(); it.hasNext(); ) {
            String arg = it.next();
            if (arg.isEmpty() || arg.charAt(0) != '@') {
                continue;
            }

            List<ServerPlayer> matchedPlayers;
            try {
                matchedPlayers = EntityArgument.entities().parse(new StringReader(arg)).findPlayers(atAllowedSource);
            } catch (CommandSyntaxException e) {
                this.plugin.getLogger().warn("Error parsing selector '" + arg + "' for " + source + " executing " + args, e);
                continue;
            }

            if (matchedPlayers.isEmpty()) {
                continue;
            }

            if (matchedPlayers.size() > 1) {
                this.plugin.getLogger().warn("Error parsing selector '" + arg + "' for " + source + " executing " + args +
                        ": ambiguous result (more than one player matched) - " + matchedPlayers);
                continue;
            }

            ServerPlayer player = matchedPlayers.get(0);
            it.set(player.getStringUUID());
        }

        return args;
    }
}
