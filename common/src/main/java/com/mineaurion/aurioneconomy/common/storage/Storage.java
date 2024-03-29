package com.mineaurion.aurioneconomy.common.storage;

import com.mineaurion.aurioneconomy.common.misc.Throwing;
import com.mineaurion.aurioneconomy.common.model.Account;
import com.mineaurion.aurioneconomy.common.model.Transaction;
import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;
import com.mineaurion.aurioneconomy.common.storage.database.StorageImplementation;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class Storage {

    private final AurionEconomyPlugin plugin;
    private final StorageImplementation implementation;

    public Storage(AurionEconomyPlugin plugin, StorageImplementation implementation){
        this.plugin = plugin;
        this.implementation = implementation;
    }

    public <T> CompletableFuture<T> future(Callable<T> supplier){
        return CompletableFuture.supplyAsync(() -> {
            try {
                return supplier.call();
            } catch (Exception e){
                if(e instanceof RuntimeException){
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, this.plugin.getBootstrap().getScheduler().async());
    }

    private CompletableFuture<Void> future(Throwing.Runnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                }
                throw new CompletionException(e);
            }
        }, this.plugin.getBootstrap().getScheduler().async());
    }


    public String getName(){
        return this.implementation.getImplementationName();
    }

    public void init(){
        try {
            this.implementation.init();
        } catch (Exception e){
            this.plugin.getLogger().severe("Failed to init storage implementation", e);
        }
    }

    public void shutdown(){
        try {
            this.implementation.shutdown();
        } catch (Exception e){
            // TODO: better logging
            System.out.println("Failed to shutdown storage implementation");
            System.out.println(e.getMessage());
        }
    }

    public CompletableFuture<Void> createAccount(UUID uuid){
        return future(() -> this.implementation.createAccount(uuid));
    }

    public CompletableFuture<List<Account>> listAccounts(){
        return future(this.implementation::listAccounts);
    }

    /**
     *
     * @return return an integer with the amount, otherwise null if player not in storage
     */
    public CompletableFuture<Integer> getBalance(UUID uuid){
        return future(() -> this.implementation.getBalance(uuid));
    }

    public CompletableFuture<Void> addMount(Transaction transaction){
        return future(() -> {
            this.implementation.addAmount(transaction.getReceiver().getUUID(), transaction.getAmount());
            this.implementation.addTransactions(transaction);
        });
    }

    public CompletableFuture<Void> setAmount(Transaction transaction){
        return future(() -> {
            this.implementation.setAmount(transaction.getReceiver().getUUID(), transaction.getAmount());
            this.implementation.addTransactions(transaction);
        });
    }

    public CompletableFuture<Void> withdrawAmount(Transaction transaction){
        return future(() -> {
            this.implementation.withdrawAmount(transaction.getReceiver().getUUID(), transaction.getAmount());
            this.implementation.addTransactions(transaction);
        });
    }

    public CompletableFuture<Void> playerToPlayer(Transaction transaction){
        return future(() -> {
            this.implementation.withdrawAmount(transaction.getSender().getUUID(), transaction.getAmount());
            this.implementation.addAmount(transaction.getReceiver().getUUID(), transaction.getAmount());
            this.implementation.addTransactions(transaction);
        });
    }

    public CompletableFuture<Boolean> checkHasEnough(UUID uuid, int amount){
        return future(() -> this.implementation.checkHasEnough(uuid, amount));
    }
}
