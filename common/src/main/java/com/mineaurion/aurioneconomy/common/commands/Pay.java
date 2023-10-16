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

public class Pay extends SingleCommand {

    public Pay(){
        super(CommandSpec.PAY, "Pay", "economy.money.pay", Predicates.inRange(1,2));
    }

    @Override
    public void execute(AurionEconomyPlugin plugin, Sender sender, List<String> args, String label) throws Exception {
        if(!sender.hasPermission(getPermission())){
            Message.COMMAND_NO_PERMISSION.send(sender);
            return;
        }

        UUID senderUUID = sender.getUUID();
        Optional<UUID> targetUUID = getPlayerUUID(plugin, args, 1);
        String targetUsername = args.get(1);
        if(!targetUUID.isPresent()){
            Message.PLAYER_NOT_FOUND.send(sender, args.get(1));
            return;
        }

        if(plugin.getStorage().getBalance(targetUUID.get()) == null){
            Message.PAY_TARGET_NOTEXIST.send(sender, targetUUID.toString());
            return;
        }

        int amount = Integer.parseInt(args.get(2));

        if(plugin.getStorage().checkHasEnough(senderUUID, amount).join()){
            Transaction transaction = new Transaction(
                sender,
                new Subject(targetUsername, targetUUID.get()),
                    amount,
                    TransactionType.PLAYER_TO_PLAYER,
                    ""
            );
            plugin.getStorage().playerToPlayer(transaction).join();
            Message.PAY_SENDER.send(sender, amount, args.get(1));
            plugin.sendMessageToSpecificPlayer(targetUUID.get(), Message.PAY_TARGET.build(amount, sender.getName()));
        } else {
            Message.NOT_ENOUGH_CURRENCY.send(sender);
        }
    }

    @Override
    public List<String> tabComplete(AurionEconomyPlugin plugin, Sender sender, List<String> args) {
        return new ArrayList<>(plugin.getPlayersList());
    }
}
