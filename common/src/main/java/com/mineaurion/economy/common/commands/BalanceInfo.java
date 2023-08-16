package com.mineaurion.economy.common.commands;

import com.mineaurion.economy.common.command.CommandSpec;
import com.mineaurion.economy.common.command.SingleCommand;
import com.mineaurion.economy.common.command.sender.Sender;
import com.mineaurion.economy.common.locale.Message;
import com.mineaurion.economy.common.plugin.EconomyPlugin;

import java.util.List;
import java.util.UUID;

public class BalanceInfo extends SingleCommand {
    public BalanceInfo() {
        super(CommandSpec.BALANCE, "Balance", "economy.balance", "economy.admin.balance");
    }

    @Override
    public void execute(EconomyPlugin plugin, Sender sender, List<String> args, String label) throws Exception {
        UUID playerUUID = sender.getUUID();

        if(playerUUID == Sender.CONSOLE_UUID && args.size() < 2){
            Message.COMMAND_CONSOLE_CANT.send(sender);
            return;
        }

        if(sender.hasPermission(getAdminPermission().get())){
            playerUUID = args.size() == 2 ? UUID.fromString(args.get(1)) : sender.getUUID();
        }

        String username = plugin.lookupUsername(playerUUID).orElse("no username");
        Integer balance = plugin.getStorage().getBalance(playerUUID).join();

        //TODO: move it in loginListener maybe
        if(balance == null){
            plugin.getLogger().info("Player doesn't have an account yet, creating one with default value");
            plugin.getStorage().createAccount(playerUUID).join();
        }
        Message.BALANCE_INFO.send(sender, username, balance == null ? 0 : balance);
    }

    @Override
    public void sendUsage(Sender sender, String label) {

    }
}
