package com.mineaurion.economy.common.commands;

import com.mineaurion.economy.common.command.CommandSpec;
import com.mineaurion.economy.common.command.SingleCommand;
import com.mineaurion.economy.common.command.sender.Sender;
import com.mineaurion.economy.common.locale.Message;
import com.mineaurion.economy.common.misc.Predicates;
import com.mineaurion.economy.common.plugin.EconomyPlugin;

import java.util.List;
import java.util.UUID;

public class AddAmount extends SingleCommand {

    public AddAmount(){
        super(CommandSpec.ADD, "Add", "economy.money.admin.add", Predicates.inRange(1,2));
    }

    @Override
    public void execute(EconomyPlugin plugin, Sender sender, List<String> args, String label) throws Exception {
        if(!sender.hasPermission(getPermission())){
            Message.COMMAND_NO_PERMISSION.send(sender);
            return;
        }

        UUID playerUUID = UUID.fromString(args.get(1));
        int amount = Integer.parseInt(args.get(2));
        String username = plugin.lookupUsername(playerUUID).orElse("no username");

        plugin.getStorage().addMount(playerUUID, amount).join();

        Message.ADD_AMOUNT.send(sender, username, amount);
    }
}
