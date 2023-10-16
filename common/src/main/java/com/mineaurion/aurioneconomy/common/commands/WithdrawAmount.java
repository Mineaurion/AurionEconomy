package com.mineaurion.aurioneconomy.common.commands;

import com.mineaurion.aurioneconomy.common.command.CommandSpec;
import com.mineaurion.aurioneconomy.common.command.SingleCommand;
import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mineaurion.aurioneconomy.common.locale.Message;
import com.mineaurion.aurioneconomy.common.misc.Predicates;
import com.mineaurion.aurioneconomy.common.model.Subject;
import com.mineaurion.aurioneconomy.common.model.Transaction;
import com.mineaurion.aurioneconomy.common.model.TransactionType;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

        Optional<UUID> playerUUID = getPlayerUUID(plugin, args, 1);
        if(playerUUID.isPresent()){
            int amount = Integer.parseInt(args.get(2));
            String username = args.get(1);
            Transaction transaction = new Transaction(
                    sender,
                    new Subject(username, playerUUID.get()),
                    amount,
                    TransactionType.WITHDRAW,
                    ""
            );
            plugin.getStorage().withdrawAmount(transaction).join();
            Message.WITHDRAW_AMOUNT.send(sender, username, amount);
        } else {
            Message.PLAYER_NOT_FOUND.send(sender, args.get(1));
        }
    }

    @Override
    public List<String> tabComplete(AurionEconomyPlugin plugin, Sender sender, List<String> args) {
        return new ArrayList<>(plugin.getPlayersList());
    }
}
