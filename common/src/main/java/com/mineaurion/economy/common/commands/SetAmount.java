package com.mineaurion.economy.common.commands;

import com.mineaurion.economy.common.command.CommandSpec;
import com.mineaurion.economy.common.command.SingleCommand;
import com.mineaurion.economy.common.command.sender.Sender;
import com.mineaurion.economy.common.locale.Message;
import com.mineaurion.economy.common.plugin.EconomyPlugin;

import java.util.List;
import java.util.UUID;

public class SetAmount extends SingleCommand {

    public SetAmount(){
        super(CommandSpec.SET, "Set", "economy.admin.set");
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

        plugin.getStorage().setAmount(playerUUID,amount).join();
        Message.SET_AMOUNT.send(sender, username, amount);
    }

    @Override
    public void sendUsage(Sender sender, String label) {

    }
}
