package com.mineaurion.AurionEconomy.commands.money;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;

import com.mineaurion.AurionEconomy.LogInfo;
import com.mineaurion.AurionEconomy.Main;
import com.mineaurion.AurionEconomy.classEconomie.AAccount;
import com.mineaurion.AurionEconomy.classEconomie.AccountManager;

public class CMDGive implements CommandExecutor {
	private AccountManager accountManager;

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		accountManager = Main.getInstance().getAccountManager();

		User player = args.<User>getOne("player").get();
		String strAmount = args.<String>getOne("montant").get();
		Pattern amountPattern = Pattern.compile("^[+]?(\\d*\\.)?\\d+$");
		Matcher m = amountPattern.matcher(strAmount);

		if (m.matches()) {
			BigDecimal amount = new BigDecimal(strAmount).setScale(2, BigDecimal.ROUND_DOWN);
			AAccount playeraccount = (AAccount) accountManager.getOrCreateAccount(player.getUniqueId()).get();
			Currency defaultCurrency = accountManager.getDefaultCurrency();
			Text amountText = defaultCurrency.format(amount);

			TransactionResult transactionresult = playeraccount.deposit(defaultCurrency, amount,
					Cause.of(NamedCause.of("Aurions", Main.getInstance().getPlugin())));
			if (transactionresult.getResult() == ResultType.SUCCESS) {
				DateTime dateTime = DateTime.now(DateTimeZone.forID("Europe/Paris"));
				Main.writeLog(player.getName(), LogInfo.GIVE, Cause.of(NamedCause.of("AurionsEconomy", "Sponge")), dateTime, amount.doubleValue());
				Main.sendmessage("Tu as donné {{RED}}" + amountText + "{{WHITE}} à {{YELLOW}}" + player.getName(),
						src.getName());

				if (player.isOnline()) {
					Main.sendmessage("Tu as reçu {{RED}}" + amountText + "", player.getName());
				}
				return CommandResult.success();
			}
		} else {
			throw new CommandException(Text.of("Invalid amount! Must be a positive number!"));
		}
		return CommandResult.empty();
	}

}
