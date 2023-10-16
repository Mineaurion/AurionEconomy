package com.mineaurion.aurioneconomy.common.commands;

import com.mineaurion.aurioneconomy.common.command.CommandSpec;
import com.mineaurion.aurioneconomy.common.command.SingleCommand;
import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mineaurion.aurioneconomy.common.locale.Message;
import com.mineaurion.aurioneconomy.common.misc.Predicates;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BalanceInfo extends SingleCommand {
    public BalanceInfo() {
        super(CommandSpec.BALANCE, "Balance", "economy.money.balance", "economy.money.admin.balance", Predicates.alwaysFalse());
    }

    @Override
    public void execute(AurionEconomyPlugin plugin, Sender sender, List<String> args, String label) throws Exception {
        UUID playerUUID = sender.getUUID();

        if(playerUUID == Sender.CONSOLE_UUID && args.size() < 2){
            sendUsage(sender, label);
            return;
        }
        String username = sender.getName();

        if(sender.hasPermission(getAdminPermission().get()) && args.size() == 2){
            playerUUID = getPlayerUUID(plugin, args, 1).orElse(sender.getUUID());
            username = args.get(1);

        }

        Integer balance = plugin.getStorage().getBalance(playerUUID).join();

        //TODO: move it in loginListener maybe
        if(balance == null){
            plugin.getLogger().info("Player doesn't have an account yet, creating one with default value");
            plugin.getStorage().createAccount(playerUUID).join();
        }
        Message.BALANCE_INFO.send(sender, username, balance == null ? 0 : balance);
    }

    @Override
    public List<String> tabComplete(AurionEconomyPlugin plugin, Sender sender, List<String> args) {
        if(sender.hasPermission(getAdminPermission().get())){
            return new ArrayList<>(plugin.getPlayersList());
        }
        return Collections.emptyList();
    }
}
