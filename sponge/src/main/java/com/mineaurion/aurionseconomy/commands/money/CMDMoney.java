package com.mineaurion.aurionseconomy.commands.money;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import com.mineaurion.aurionseconomy.Main;
import com.mineaurion.aurionseconomy.classEconomie.AAccount;
import com.mineaurion.aurionseconomy.classEconomie.AccountManager;

public class CMDMoney implements CommandExecutor {
	private AccountManager accountManager;
	
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		accountManager = Main.getInstance().getAccountManager();
			if (src instanceof Player) {
				Player sender = (Player) src;
				AAccount playerAccount = (AAccount) accountManager.getOrCreateAccount(sender.getUniqueId()).get();
				Currency defaultCurrency = accountManager.getDefaultCurrency();
				Text playerBalance = defaultCurrency.format(playerAccount.getBalance(defaultCurrency));
				Main.sendmessage("Balance : " + playerBalance.toPlainSingle(),
						src.getName());
				return CommandResult.success();
			}else{
				Main.sendmessage("La console na pas de monney, utilise /money balance <Player>", "console");
				return CommandResult.success();
			}
	}

}
