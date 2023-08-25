package com.mineaurion.aurioneconomy.common.commands;

import com.mineaurion.aurioneconomy.common.command.CommandSpec;
import com.mineaurion.aurioneconomy.common.command.SingleCommand;
import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mineaurion.aurioneconomy.common.locale.Message;
import com.mineaurion.aurioneconomy.common.misc.Predicates;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AddAmount extends SingleCommand {

    public AddAmount(){
        super(CommandSpec.ADD, "Add", "economy.money.admin.add", Predicates.inRange(1,2));
    }

    @Override
    public void execute(AurionEconomyPlugin plugin, Sender sender, List<String> args, String label) throws Exception {
        if(!sender.hasPermission(getPermission())){
            Message.COMMAND_NO_PERMISSION.send(sender);
            return;
        }

        Optional<UUID> playerUUID = getPlayerUUID(plugin, args, 1);
        if(playerUUID.isPresent()){
            int amount = Integer.parseInt(args.get(2));
            String username = args.get(1);
            plugin.getStorage().addMount(playerUUID.get(), amount).join();
            Message.ADD_AMOUNT.send(sender, username, amount);
        } else {
            Message.PLAYER_NOT_FOUND.send(sender, args.get(1));
        }
    }
}