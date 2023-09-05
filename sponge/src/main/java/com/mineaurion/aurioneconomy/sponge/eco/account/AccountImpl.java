package com.mineaurion.aurioneconomy.sponge.eco.account;

import com.mineaurion.aurioneconomy.sponge.AurionEconomy;
import com.mineaurion.aurioneconomy.sponge.eco.CurrencyImpl;
import com.mineaurion.aurioneconomy.sponge.eco.TransactionResultImpl;
import net.kyori.adventure.text.Component;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionException;

public class AccountImpl implements Account, UniqueAccount {
    private final UUID uuid;
    protected boolean isVirtual;
    private final AurionEconomy plugin;
    private final CurrencyImpl currency;


    public AccountImpl(AurionEconomy plugin, CurrencyImpl currency, UUID uuid){
        this.plugin = plugin;
        this.currency = currency;
        this.uuid = uuid;
        this.isVirtual = false;
    }
    
    @Override
    public Component displayName() {
        return Component.text(uuid.toString());
    }

    @Override
    public BigDecimal defaultBalance(Currency currency) {
        //TODO: need to work on this, maybe put this onto abstract class or cfg
        return BigDecimal.ZERO;
    }

    @Override
    public boolean hasBalance(Currency currency) {
        return plugin.getStorage().getBalance(uuid).join() != null;
    }

    @Override
    public boolean hasBalance(Currency currency, Set<Context> contexts) {
        return hasBalance(currency);
    }

    @Override
    public boolean hasBalance(Currency currency, Cause cause) {
        return hasBalance(currency);
    }

    @Override
    public BigDecimal balance(Currency currency) {
        int balance = 0;
        if(hasBalance(currency)){
            balance = plugin.getStorage().getBalance(uuid).join();
        }
        return new BigDecimal(balance);
    }

    @Override
    public BigDecimal balance(Currency currency, Set<Context> contexts) {
        return balance(currency);
    }

    @Override
    public BigDecimal balance(Currency currency, Cause cause) {
        return balance(currency);
    }

    @Override
    public Map<Currency, BigDecimal> balances() {
        return new HashMap<Currency, BigDecimal>(){{
            put(currency, balance(currency));
        }};
    }

    @Override
    public Map<Currency, BigDecimal> balances(Set<Context> contexts) {
        return balances();
    }

    @Override
    public Map<Currency, BigDecimal> balances(Cause cause) {
        return balances();
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount) {
        TransactionResultImpl.Builder result = TransactionResultImpl.builder()
                .setCurrency(currency)
                .setAccount(this)
                .setTransactionType(TransactionTypes.DEPOSIT.get())
                .setAmount(amount);
        if(hasBalance(currency) && amount.compareTo(BigDecimal.ZERO) >= 0){
            if(amount.equals(BigDecimal.valueOf(Integer.MAX_VALUE))){
                return TransactionResultImpl.builder()
                        .setCurrency(currency)
                        .setAccount(this)
                        .setAmount(amount)
                        .setResultType(ResultType.CONTEXT_MISMATCH)
                        .build();
            }

            try {
                plugin.getStorage().setAmount(uuid, amount.intValue()).join();
                return result.setResultType(ResultType.SUCCESS).build();
            } catch (CompletionException e){
                return result.setResultType(ResultType.FAILED).build();
            }
        } else {
            return result.setResultType(ResultType.ACCOUNT_NO_FUNDS).build();
        }
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Set<Context> contexts) {
        return ((TransactionResultImpl) setBalance(currency, amount)).toBuilder().setContexts(contexts).build();
    }

    @Override
    public TransactionResult setBalance(Currency currency, BigDecimal amount, Cause cause) {
        return ((TransactionResultImpl) setBalance(currency, amount)).toBuilder().setCause(cause).build();
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances() {
        TransactionResultImpl resetResult = TransactionResultImpl.builder()
                .setResultType(ResultType.FAILED)
                .setTransactionType(TransactionTypes.TRANSFER.get())
                .build();
        return new HashMap<Currency, TransactionResult>() {{
            put(currency, resetResult);
        }};
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Set<Context> contexts) {
        return resetBalances();
    }

    @Override
    public Map<Currency, TransactionResult> resetBalances(Cause cause) {
        return resetBalances();
    }

    @Override
    public TransactionResult resetBalance(Currency currency) {
        return TransactionResultImpl.builder()
                .setResultType(ResultType.FAILED)
                .setTransactionType(TransactionTypes.TRANSFER.get())
                .build();
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Set<Context> contexts) {
        return ((TransactionResultImpl) resetBalance(currency)).toBuilder().setContexts(contexts).build();
    }

    @Override
    public TransactionResult resetBalance(Currency currency, Cause cause) {
        return ((TransactionResultImpl) resetBalance(currency)).toBuilder().setCause(cause).build();
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount) {
        BigDecimal balance = balance(currency);
        BigDecimal newBalance = balance.add(amount);
        return setBalance(currency, newBalance);
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Set<Context> contexts) {
        return ((TransactionResultImpl) deposit(currency, amount)).toBuilder().setContexts(contexts).build();
    }

    @Override
    public TransactionResult deposit(Currency currency, BigDecimal amount, Cause cause) {
        return ((TransactionResultImpl) deposit(currency, amount)).toBuilder().setCause(cause).build();
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount) {
        BigDecimal balance = balance(currency);
        BigDecimal newBalance = balance.subtract(amount);
        if(balance.compareTo(amount) >= 0){
            return setBalance(currency, newBalance);
        } else {
            return TransactionResultImpl.builder()
                    .setCurrency(currency)
                    .setAccount(this)
                    .setTransactionType(TransactionTypes.WITHDRAW.get())
                    .setAmount(amount)
                    .build();
        }
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Set<Context> contexts) {
        return ((TransactionResultImpl) withdraw(currency, amount)).toBuilder().setContexts(contexts).build();
    }

    @Override
    public TransactionResult withdraw(Currency currency, BigDecimal amount, Cause cause) {
        return ((TransactionResultImpl) withdraw(currency, amount)).toBuilder().setCause(cause).build();
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount) {
        TransactionResultImpl.Builder result = TransactionResultImpl.builder()
                .setAccount(this)
                .setTransactionType(TransactionTypes.TRANSFER.get())
                .setAmount(amount)
                .setCurrency(currency);
        if(hasBalance(currency) && to.hasBalance(currency)){
            BigDecimal balance = balance(currency);
            BigDecimal newBalance = balance.subtract(amount);
            if(newBalance.compareTo(BigDecimal.ZERO) >= 0){
                try {
                    TransactionResult withDrawResult = this.withdraw(currency, amount);
                    TransactionResult depositResult = to.deposit(currency, amount);
                    if(withDrawResult.result().equals(ResultType.SUCCESS) && depositResult.result().equals(ResultType.SUCCESS)){
                        return result.setResultType(ResultType.SUCCESS).build().toTransferResult(to);
                    } else {
                        throw new Exception("error occured during transfer");
                    }
                } catch (Exception e){
                    e.printStackTrace();
                    return result.setResultType(ResultType.FAILED).build().toTransferResult(to);
                }
            } else {
                return result.setResultType(ResultType.ACCOUNT_NO_FUNDS).build().toTransferResult(to);
            }
        } else {
            return result.setResultType(ResultType.CONTEXT_MISMATCH).build().toTransferResult(to);
        }
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Set<Context> contexts) {
        return ((TransactionResultImpl.TransferResultImpl) transfer(to, currency, amount)).withContext(contexts);
    }

    @Override
    public TransferResult transfer(Account to, Currency currency, BigDecimal amount, Cause cause) {
        return ((TransactionResultImpl.TransferResultImpl) transfer(to, currency, amount)).withCause(cause);
    }

    @Override
    public String identifier() {
        return uuid.toString();
    }

    @Override
    public UUID uniqueId() {
        return this.uuid;
    }

    public boolean isVirtual(){
        return this.isVirtual;
    }

}
