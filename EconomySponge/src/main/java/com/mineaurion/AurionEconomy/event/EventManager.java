package com.mineaurion.AurionEconomy.event;

import java.math.BigDecimal;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import com.mineaurion.AurionEconomy.Main;
import com.mineaurion.AurionEconomy.Mysql.MySQLEngine;

public class EventManager {

	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		Main.getInstance();
		if (!Main.rootNode.getNode("Setup").getBoolean() && event.getTargetEntity().hasPermission("*")) {
			Main.getInstance();
			Main.sendmessage(
					"{{DARK_RED}}Configure le plugin (pense a changer le Setup en true) puis fait /economieadmin reload",
					event.getTargetEntity().getName());
		}

		if (Main.rootNode.getNode("Setup").getBoolean()) {
			Player player = event.getTargetEntity();
			if (!MySQLEngine.accountExist(player.getUniqueId().toString())) {
				MySQLEngine.createaccount(player.getUniqueId().toString(), player.getName(),
						Main.getInstance().getHoldings());
			} else {
				MySQLEngine.updateUsername(player.getName(), player.getUniqueId().toString());
				Main.getInstance().getAccountManager().getOrCreateAccount(player.getUniqueId()).get().setBalance(
						Main.getInstance().getDefaultCurrency(),
						new BigDecimal(MySQLEngine.getBalance(player.getUniqueId().toString(),true)),
						Cause.of(NamedCause.of("AurionsEconomy", Main.getInstance().getPlugin())));
			}
		}

	}
}
