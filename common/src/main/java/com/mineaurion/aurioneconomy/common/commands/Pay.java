package com.mineaurion.aurioneconomy.common.commands;

import com.mineaurion.aurioneconomy.common.command.CommandSpec;
import com.mineaurion.aurioneconomy.common.command.SingleCommand;
import com.mineaurion.aurioneconomy.common.locale.Message;
import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mineaurion.aurioneconomy.common.misc.Predicates;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Pay extends SingleCommand {

    public Pay(){
        super(CommandSpec.PAY, "Pay", "economy.pay", Predicates.inRange(1,2));
    }

    @Override
    public void execute(AurionEconomyPlugin plugin, Sender sender, List<String> args, String label) throws Exception {
        if(!sender.hasPermission(getPermission())){
            Message.COMMAND_NO_PERMISSION.send(sender);
            return;
        }

        UUID senderUUID = sender.getUUID();
        UUID targetUUID = UUID.fromString(args.get(1));
        int amount = Integer.parseInt(args.get(2));

        if(plugin.getStorage().getBalance(targetUUID) == null){
            Message.PAY_TARGET_NOTEXIST.send(sender, targetUUID.toString());
            return;
        }

        if(plugin.getStorage().checkHasEnough(senderUUID, amount).join()){
            CompletableFuture.allOf(
                    plugin.getStorage().withdrawAmount(senderUUID, amount),
                    plugin.getStorage().addMount(targetUUID, amount)
            ).join();
            Message.PAY_SENDER.send(sender, amount, plugin.lookupUsername(targetUUID).orElse("-"));
            plugin.sendMessageToSpecificPlayer(targetUUID, Message.PAY_TARGET.build(amount, sender.getName()));
        } else {
            Message.NOT_ENOUGH_CURRENCY.send(sender);
        }
    }
}
