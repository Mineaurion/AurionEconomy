package com.mineaurion.aurioneconomy.bukkit.vault;

import com.mineaurion.aurioneconomy.bukkit.AurionEconomy;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import com.mineaurion.aurioneconomy.common.storage.Storage;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.OfflinePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionException;

public class VaultConnector implements Economy {

    public Currency currency;
    private AurionEconomy plugin;
    private Storage storage;

    public VaultConnector(AurionEconomy plugin){
        this.currency = new Currency("Dollar", "Dollars", "$", 0, true);
        this.plugin = plugin;
        this.storage = this.plugin.getStorage();
    }
    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public String getName() {
        return AurionEconomyPlugin.MOD_ID;
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return String.valueOf(amount);
    }

    @Override
    public String currencyNamePlural() {
        return currency.getPluralDisplayName();
    }

    @Override
    public String currencyNameSingular() {
        return currency.getDisplayName();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasAccount(String playerName) {
        Optional<UUID> uuid = this.plugin.lookupUUID(playerName);
        return uuid.filter(value -> this.storage.getBalance(value).join() != null).isPresent();
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        UUID uuid = player.getPlayer().getUniqueId();
        return this.storage.getBalance(uuid).join() != null;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        Optional<UUID> uuid = this.plugin.lookupUUID(playerName);
        double balance = 0;
        if(hasAccount(playerName) && uuid.isPresent()){
            balance = this.storage.getBalance(uuid.get()).join().doubleValue();
        }
        return balance;
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        return getBalance(player.getName());
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player.getName());
    }

    @Override
    public boolean has(String playerName, double amount) {
        double balance = getBalance(playerName);
        return balance > amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        return has(player.getName(), amount);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player.getName(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        //TODO: get caller of the method to get the plugin name ? for the log
        double balance = getBalance(playerName);
        if(has(playerName, amount)){
            return new EconomyResponse(0, balance, ResponseType.FAILURE, "Insufficient funds");
        }
        double newBalance = balance - amount;
        UUID uuid = this.plugin.lookupUUID(playerName).get();

        return setBalance(uuid, newBalance, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        return withdrawPlayer(player.getName(), amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player.getName(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        double balance = getBalance(playerName);
        double newBalance = balance - amount;
        UUID uuid = this.plugin.lookupUUID(playerName).get();
        return setBalance(uuid, newBalance, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        return depositPlayer(player.getName(), amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player.getName(), amount);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, ResponseType.NOT_IMPLEMENTED, "Not Implemented");
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return createBank(name, player.getName());
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0,0 , ResponseType.NOT_IMPLEMENTED, "Not Implemented");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0,0 , ResponseType.NOT_IMPLEMENTED, "Not Implemented");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0,0 , ResponseType.NOT_IMPLEMENTED, "Not Implemented");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0,0 , ResponseType.NOT_IMPLEMENTED, "Not Implemented");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0,0 , ResponseType.NOT_IMPLEMENTED, "Not Implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0,0 , ResponseType.NOT_IMPLEMENTED, "Not Implemented");
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return new EconomyResponse(0,0 , ResponseType.NOT_IMPLEMENTED, "Not Implemented");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0,0 , ResponseType.NOT_IMPLEMENTED, "Not Implemented");
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return new EconomyResponse(0,0 , ResponseType.NOT_IMPLEMENTED, "Not Implemented");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return hasAccount(player);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    private EconomyResponse setBalance(UUID uuid, double newBalance, double amount){
        if(newBalance >= 0){
            try {
                this.storage.setAmount(uuid, (int) newBalance).join();
                return new EconomyResponse(amount, newBalance, ResponseType.SUCCESS, null);
            } catch (CompletionException e){
                return new EconomyResponse(0, newBalance, ResponseType.FAILURE, "Error Mysql");
            }
        } else {
            return new EconomyResponse(0, newBalance, ResponseType.FAILURE, "Error newer balance need to be > 0");
        }
    }
}
