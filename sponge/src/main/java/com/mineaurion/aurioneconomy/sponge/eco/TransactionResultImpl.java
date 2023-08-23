package com.mineaurion.aurioneconomy.sponge.eco;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.Account;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import java.math.BigDecimal;
import java.util.Set;

public class TransactionResultImpl implements TransactionResult {

    protected final Account account;
    protected final Currency currency;
    protected final BigDecimal amount;
    protected final TransactionType transactionType;
    protected final ResultType resultType;
    @Nullable protected Set<Context> contexts;
    @Nullable protected Cause cause;

    public static Builder builder(){
        return Sponge.game().builderProvider().provide(Builder.class);
    }

    private TransactionResultImpl(
            Account account,
            Currency currency,
            BigDecimal amount,
            @Nullable Set<Context> contexts,
            TransactionType transactionType,
            ResultType resultType,
            @Nullable Cause cause
    ) {
        this.account = account;
        this.currency = currency;
        this.amount = amount;
        this.contexts = contexts;
        this.transactionType = transactionType;
        this.resultType = resultType;
        this.cause = cause;
    }

    @Override
    public Account account() {
        return this.account;
    }

    @Override
    public Currency currency() {
        return this.currency;
    }

    @Override
    public BigDecimal amount() {
        return this.amount;
    }

    @Override
    public Set<Context> contexts() {
        return this.contexts;
    }

    @Override
    public ResultType result() {
        return this.resultType;
    }

    @Nullable
    @Override
    public TransactionType type() {
        return this.transactionType;
    }

    @Nullable
    public Cause getCause() {
        return this.cause;
    }

    public Builder toBuilder(){
        Builder builder = new Builder();
        builder
                .setAccount(this.account)
                .setAmount(amount)
                .setCurrency(currency)
                .setTransactionType(transactionType)
                .setResultType(resultType)
                .setCause(cause)
                .setContexts(contexts)
        ;
        return builder;
    }

    public TransferResultImpl toTransferResult(Account accountTo){
        return new TransferResultImpl(this, accountTo);
    }

    public static class Builder implements org.spongepowered.api.util.Builder<TransactionResultImpl, Builder> {
        protected Account account;
        protected Currency currency;
        protected BigDecimal amount;
        protected TransactionType transactionType;
        protected ResultType resultType;
        protected Set<Context> contexts;
        protected Cause cause;

        public Account getAccount() {
            return account;
        }

        public Builder setAccount(Account account) {
            this.account = account;
            return this;
        }

        public Currency getCurrency() {
            return currency;
        }

        public Builder setCurrency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public Builder setAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public TransactionType getTransactionType() {
            return transactionType;
        }

        public Builder setTransactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }

        public ResultType getResultType() {
            return resultType;
        }

        public Builder setResultType(ResultType resultType) {
            this.resultType = resultType;
            return this;
        }

        public Set<Context> getContexts() {
            return contexts;
        }

        public Builder setContexts(Set<Context> contexts) {
            this.contexts = contexts;
            return this;
        }

        public Cause getCause() {
            return cause;
        }

        public Builder setCause(Cause cause) {
            this.cause = cause;
            return this;
        }

        @Override
        public @NotNull TransactionResultImpl build() {
            return new TransactionResultImpl(
                    this.account,
                    this.currency,
                    this.amount,
                    this.contexts,
                    this.transactionType,
                    this.resultType,
                    this.cause
            );
        }
    }

    public static class TransferResultImpl extends TransactionResultImpl implements TransferResult {
        private final Account accountTo;

        private TransferResultImpl(TransactionResultImpl result, Account accountTo){
            super(result.account, result.currency, result.amount, result.contexts, result.transactionType, result.resultType, result.cause);
            this.accountTo = accountTo;
        }

        public TransferResultImpl withContext(Set<Context> contexts){
            this.contexts = contexts;
            return this;
        }

        public TransferResultImpl withCause(Cause cause){
            this.cause = cause;
            return this;
        }

        @Override
        public Account accountTo() {
            return this.accountTo;
        }
    }
}
