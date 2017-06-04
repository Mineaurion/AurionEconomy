package com.mineaurion.EconomyBukkit.command.monney;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.mineaurion.EconomyBukkit.Main;
import com.mineaurion.EconomyBukkit.Mysql.MySQLEngine;

public class CMDInfinite {

	public CMDInfinite(CommandSender sender, String[] args) {
		if(args.length==3){
			if(args[2].equalsIgnoreCase("true")||args[2].equalsIgnoreCase("false")){
				boolean bool = Boolean.valueOf(args[2]);
				@SuppressWarnings("deprecation")
				Player player = Bukkit.getPlayer(args[1]);
				if(MySQLEngine.setInfiniteMoney(player.getUniqueId().toString(), bool)){
					if(bool){
						Main.getInstance().econ.depositPlayer(player, Double.MAX_VALUE);
					}else{
						Main.getInstance().econ.depositPlayer(player, MySQLEngine.getBalance(player.getUniqueId().toString(), false));
					}
					Main.sendmessage("Infiny monney pour "+player.getName()+" : {{RED}}"+String.valueOf(bool), sender.getName());
					
				}else{
					Main.sendmessage("une erreur est survenu check la console", sender.getName());
				
				}
			}else{
				Main.sendmessage("{{RED}}Use /money infinite <player> <true|false>", sender.getName());
			}
		}else{
			Main.sendmessage("{{RED}}Use /money infinite <player> <true|false>", sender.getName());
		}
	}

}
