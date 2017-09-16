package com.mineaurion.aurionseconomy.command.monney;

import org.bukkit.command.CommandSender;

import com.mineaurion.aurionseconomy.Mysql.MySQLEngine;

public class CMDTop {

	public CMDTop(CommandSender sender, String[] args) {
		MySQLEngine.Balancetop(sender);
	}

}
