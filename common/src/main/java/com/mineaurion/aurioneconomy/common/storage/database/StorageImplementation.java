package com.mineaurion.aurioneconomy.common.storage.database;

import com.mineaurion.aurioneconomy.common.model.Account;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

import java.util.List;
import java.util.UUID;

public interface StorageImplementation {

    AurionEconomyPlugin getPlugin();

    String getImplementationName();

    void init() throws Exception;

    void shutdown();

    void createAccount(UUID uuid) throws Exception;

    List<Account> listAccounts() throws Exception;

    Integer getBalance(UUID uuid) throws Exception;
    void addAmount(UUID uuid, int amount) throws Exception;
    void setAmount(UUID uuid, int amount) throws Exception;
    void withdrawAmount(UUID uuid, int amount) throws Exception;
    boolean checkHasEnough(UUID uuid, int amountToCheck) throws Exception;
}
