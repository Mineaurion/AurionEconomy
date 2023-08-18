package com.mineaurion.aurioneconomy.common.commands;

import com.mineaurion.aurioneconomy.common.command.CommandSpec;
import com.mineaurion.aurioneconomy.common.command.SingleCommand;
import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mineaurion.aurioneconomy.common.locale.Message;
import com.mineaurion.aurioneconomy.common.misc.Predicates;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

import java.util.List;
import java.util.UUID;

public class WithdrawAmount extends SingleCommand {

    public WithdrawAmount(){
        super(CommandSpec.WITHDRAW, "Withdraw", "economy.money.admin.withdraw", Predicates.inRange(1,2));
    }

    @Override
    public void execute(AurionEconomyPlugin plugin, Sender sender, List<String> args, String label) throws Exception {
        if(!sender.hasPermission(getPermission())){
            Message.COMMAND_NO_PERMISSION.send(sender);
            return;
        }

        UUID playerUUID = UUID.fromString(args.get(1));
        int amount = Integer.parseInt(args.get(2));
        String username = plugin.lookupUsername(playerUUID).orElse("no username");

        plugin.getStorage().withdrawAmount(playerUUID, amount).join();

        Message.WITHDRAW_AMOUNT.send(sender, username, amount);
    }
}
