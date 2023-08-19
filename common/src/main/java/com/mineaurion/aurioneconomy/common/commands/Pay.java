package com.mineaurion.aurioneconomy.common.commands;

import com.mineaurion.aurioneconomy.common.command.CommandSpec;
import com.mineaurion.aurioneconomy.common.command.SingleCommand;
import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import com.mineaurion.aurioneconomy.common.locale.Message;
import com.mineaurion.aurioneconomy.common.misc.Predicates;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

import java.util.List;
import java.util.Optional;
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
        Optional<UUID> targetUUID = getPlayerUUID(plugin, args, 1);
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
            CompletableFuture.allOf(
                    plugin.getStorage().withdrawAmount(senderUUID, amount),
                    plugin.getStorage().addMount(targetUUID.get(), amount)
            ).join();
            Message.PAY_SENDER.send(sender, amount, args.get(1));
            plugin.sendMessageToSpecificPlayer(targetUUID.get(), Message.PAY_TARGET.build(amount, sender.getName()));
        } else {
            Message.NOT_ENOUGH_CURRENCY.send(sender);
        }
    }
}
