package com.mineaurion.aurioneconomy.sponge.eco;

import com.mineaurion.aurioneconomy.sponge.AurionEconomy;
import com.mineaurion.aurioneconomy.sponge.eco.account.AccountImpl;
import org.spongepowered.api.service.economy.account.*;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EconomyService implements org.spongepowered.api.service.economy.EconomyService {
    private final AurionEconomy plugin;
    private final CurrencyImpl currency;

    public EconomyService(AurionEconomy plugin){
        this.plugin = plugin;
        this.currency = new CurrencyImpl("Dollar", "Dollars", "$", 0, true);
    }

    @Override
    public CurrencyImpl defaultCurrency() {
        return currency;
    }

    @Override
    public Optional<UniqueAccount> findOrCreateAccount(UUID uuid) {
        AccountImpl account = new AccountImpl(plugin, currency, uuid);
        if(!hasAccount(uuid)){
            plugin.getStorage().createAccount(uuid);
        }
        return Optional.of(account);
    }

    @Override
    public Optional<Account> findOrCreateAccount(String identifier) {
        return Optional.empty();
    }


    @Override
    public boolean hasAccount(UUID uuid) {
        return plugin.getStorage().getBalance(uuid).join() != null;
    }

    @Override
    public boolean hasAccount(String identifier) {
        return false;
    }

    @Override
    public Stream<UniqueAccount> streamUniqueAccounts() {
        return plugin.getStorage().listAccounts().join().stream().map(account -> new AccountImpl(plugin, currency, account.getUUID()));
    }

    @Override
    public Collection<UniqueAccount> uniqueAccounts() {
        return plugin.getStorage().listAccounts().join().stream().map(account -> new AccountImpl(plugin, currency, account.getUUID())).collect(Collectors.toList());
    }

    @Override
    public Stream<VirtualAccount> streamVirtualAccounts() {
        return null;
    }

    @Override
    public Collection<VirtualAccount> virtualAccounts() {
        return null;
    }

    @Override
    public AccountDeletionResultType deleteAccount(UUID uuid) {
        return AccountDeletionResultTypes.ABSENT.get();
    }

    @Override
    public AccountDeletionResultType deleteAccount(String identifier) {
        return AccountDeletionResultTypes.ABSENT.get();
    }
}

