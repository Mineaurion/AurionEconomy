package com.mineaurion.economy.common.storage.database;

import com.mineaurion.economy.common.action.Action;
import com.mineaurion.economy.common.action.Log;
import com.mineaurion.economy.common.plugin.EconomyPlugin;

import java.util.UUID;

public interface StorageImplementation {

    EconomyPlugin getPlugin();

    String getImplementationName();

    void init() throws Exception;

    void shutdown();

    Integer getBalance(UUID uuid) throws Exception;
    void addAmount(UUID uuid, int amount) throws Exception;
    void setAmount(UUID uuid, int amount) throws Exception;
    void withdrawAmount(UUID uuid, int amount) throws Exception;
    boolean checkHasEnough(UUID uuid, int amountToCheck) throws Exception;

    void logAction(Action entry) throws Exception;
    Log getLog() throws Exception;
}
