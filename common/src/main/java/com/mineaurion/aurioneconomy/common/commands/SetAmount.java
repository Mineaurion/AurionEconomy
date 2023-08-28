package com.mineaurion.aurioneconomy.common.commands;

import com.mineaurion.aurioneconomy.common.command.CommandSpec;
import com.mineaurion.aurioneconomy.common.command.SingleCommand;
import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mineaurion.aurioneconomy.common.locale.Message;
import com.mineaurion.aurioneconomy.common.misc.Predicates;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SetAmount extends SingleCommand {

    public SetAmount(){
        super(CommandSpec.SET, "Set", "economy.money.admin.set", Predicates.inRange(1,2));
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
            plugin.getStorage().setAmount(playerUUID.get(), amount).join();
            Message.SET_AMOUNT.send(sender, username, amount);
        } else {
            Message.PLAYER_NOT_FOUND.send(sender, args.get(1));
        }
    }

    @Override
    public List<String> tabComplete(AurionEconomyPlugin plugin, Sender sender, List<String> args) {
        return new ArrayList<>(plugin.getPlayersList());
    }
}
