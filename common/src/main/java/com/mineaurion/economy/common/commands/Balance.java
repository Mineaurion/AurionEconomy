package com.mineaurion.economy.common.commands;

import com.mineaurion.economy.common.command.CommandSpec;
import com.mineaurion.economy.common.command.SingleCommand;
import com.mineaurion.economy.common.command.sender.Sender;
import com.mineaurion.economy.common.locale.Message;
import com.mineaurion.economy.common.plugin.EconomyPlugin;

import java.util.UUID;

public class Balance extends SingleCommand {
    public Balance() {
        super(CommandSpec.BALANCE, "Balance", "economy.balance");
    }

    @Override
    public void execute(EconomyPlugin plugin, Sender sender, String[] args, String label) throws Exception {
        UUID target = args.length == 0 ? sender.getUUID() : UUID.fromString(args[1]);
        Integer balance = plugin.getStorage().getBalance(target).join();

        if(balance == null){
            System.out.println("Player not found in database");
        }

        Message.BALANCE_INFO.send(sender, sender.getName(), balance == null ? 0 : balance);
    }

    @Override
    public void sendUsage(Sender sender, String label) {

    }
}
