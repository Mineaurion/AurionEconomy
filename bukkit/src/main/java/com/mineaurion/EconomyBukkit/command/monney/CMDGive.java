package com.mineaurion.EconomyBukkit.command.monney;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.mineaurion.EconomyBukkit.Cause;
import com.mineaurion.EconomyBukkit.LogInfo;
import com.mineaurion.EconomyBukkit.Main;
import com.mineaurion.EconomyBukkit.Mysql.MySQLEngine;

public class CMDGive {

	public CMDGive(CommandSender sender, String[] args) {
		if (args.length == 3) {
			@SuppressWarnings("deprecation")
			Player player = Bukkit.getPlayer(args[1]);
			String amount = args[2];
			Pattern amountPattern = Pattern.compile("^[+]?(\\d*\\.)?\\d+$");
			Matcher m = amountPattern.matcher(amount);
			if (m.matches() && Double.parseDouble(amount) > 0) {
				String Playerbal = Main.getInstance().format(Double.parseDouble(amount));

				double balance = Main.getInstance().econ.getBalance(player);
				double newbalance = balance + Double.parseDouble(amount);
				boolean result = MySQLEngine.setBalance(player.getUniqueId().toString(), newbalance);
				Main.getInstance().econ.depositPlayer(player, Double.parseDouble(amount));
				if (result) {
					DateTime dateTime = DateTime.now(DateTimeZone.forID("Europe/Paris"));
					Main.getInstance().writeLog(player.getName(), LogInfo.GIVE,
							Cause.VAULT, dateTime, Double.parseDouble(amount));
					Main.sendmessage("Tu as donné {{RED}}" + Playerbal + "{{WHITE}} à {{YELLOW}}" + player.getName(),
							sender.getName());
					if (player.isOnline()) {
						Main.sendmessage("Tu as reçu {{RED}}" + Playerbal + "", player.getName());
					}
				} else {
					Main.sendmessage("{{RED}}une erreur est survenu", sender.getName());
				}
			} else {
				Main.sendmessage("{{RED}}Montant invalible ou <0", sender.getName());
			}
		} else {
			Main.sendmessage("{{RED}}Use /money give <player> <montant>", sender.getName());
		}
	}

}
