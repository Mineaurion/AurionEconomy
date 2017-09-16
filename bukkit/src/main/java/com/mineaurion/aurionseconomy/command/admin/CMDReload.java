package com.mineaurion.aurionseconomy.command.admin;

import org.bukkit.command.CommandSender;

import com.mineaurion.aurionseconomy.Main;

public class CMDReload {

	public CMDReload(CommandSender sender, String[] args) {
		Main.sendmessage("{{GREEN}}Reload du plugin", sender.getName());
		Main.getInstance().load(sender.getName());
	}
}
