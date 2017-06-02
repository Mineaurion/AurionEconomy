package com.mineaurion.EconomyBukkit.command.admin;

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

public class CMDCheck {

	@SuppressWarnings("deprecation")
	public CMDCheck(CommandSender sender, String[] args) {
		if (args.length < 4) {
			Main.sendmessage("{{RED}}Use /economieadmin check <player> <montant> <command>", sender.getName());
			return;
		}
		Player player = Bukkit.getPlayer(args[1]);
		String amount = args[2];
		Pattern amountPattern = Pattern.compile("^[+]?(\\d*\\.)?\\d+$");
		Matcher m = amountPattern.matcher(amount);
		if (m.matches() && Double.parseDouble(amount) > 0) {
			String Playerbal = Main.getInstance().format(Double.parseDouble(amount));
			double balance = MySQLEngine.getBalance(player.getUniqueId().toString(),false);
			if (balance >= Double.parseDouble(amount)) {
				
				String commands = "";
				int i =3;
				while(i<args.length){
					commands = commands+" "+args[i];
					i++;
				}
				
				
				if(commands.isEmpty()){
					Main.sendmessage("{{RED}}insert une commande  executer", sender.getName());
					return;
				}
				
				double newbalance = balance - Double.parseDouble(amount);
				boolean result = MySQLEngine.setBalance(player.getUniqueId().toString(), newbalance);
				Main.getInstance().econ.withdrawPlayer(player, Double.parseDouble(amount));
				
				if (result) {
					
					String[] parts = commands.split("/");
					for (i = 0; i < parts.length; i++) {
						String command = parts[i];
						Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("<username>", player.getName()));
					}
					
					DateTime dateTime = DateTime.now(DateTimeZone.forID("Europe/Paris"));
					Main.getInstance().writeLog(player.getName(), LogInfo.CHECK, Cause.VAULT, dateTime,
							Double.parseDouble(amount));
					Main.sendmessage("Tu as retiré {{RED}}" + Playerbal + "{{WHITE}} à {{YELLOW}}" + player.getName(),
							sender.getName());
					if (player.isOnline()) {
						Main.sendmessage("Tu as dépensé {{RED}}" + Playerbal + "", player.getName());
					}
				} else {
					Main.sendmessage("{{RED}}une erreur est survenu", sender.getName());
				}
			} else {
				Main.sendmessage("{{RED}}You don't have monney", sender.getName());
			}

		} else {
			Main.sendmessage("{{RED}}Montant invalible ou <0", sender.getName());
		}
	}

}
