package com.mineaurion.aurioneconomy.common.model;

import com.mineaurion.aurioneconomy.common.command.sender.Sender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.sql.Date;

public class Transaction {

    private @NonNull Date date;
    private @NonNull Subject sender;
    private @NonNull Subject receiver;
    private @NonNull Integer amount;
    private @NonNull TransactionType type;
    private @Nullable String comments;

    public Transaction(@NonNull Date date, @NonNull Subject sender, @NonNull Subject receiver, @NonNull Integer amount, @NonNull TransactionType type, @Nullable String comments){
        this.date = date;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.type = type;
        this.comments = comments;
    }

    public Transaction(@NonNull Subject sender, @NonNull Subject receiver, @NonNull Integer amount, @NonNull TransactionType type, @Nullable String comments){
        this(
                new Date(System.currentTimeMillis()),
                sender,
                receiver,
                amount,
                type,
                comments
        );
    }

    public Transaction(@NonNull Sender sender, @NonNull Subject receiver, @NonNull Integer amount, @NonNull TransactionType type, @Nullable String comments){
        this(
                new Date(System.currentTimeMillis()),
                new Subject(sender.getName(), sender.getUUID()),
                receiver,
                amount,
                type,
                comments
        );
    }

    public @NonNull Date getDate() {
        return date;
    }

    public Transaction setDate(Date date) {
        this.date = date;
        return this;
    }

    public @NonNull Subject getSender() {
        return sender;
    }

    public Transaction setSender(Subject sender) {
        this.sender = sender;
        return this;
    }

    public @NonNull Subject getReceiver() {
        return receiver;
    }

    public Transaction setReceiver(Subject receiver) {
        this.receiver = receiver;
        return this;
    }

    public @NonNull Integer getAmount() {
        return amount;
    }

    public Transaction setAmount(Integer amount) {
        this.amount = amount;
        return this;
    }

    public @NonNull TransactionType getType() {
        return type;
    }

    public Transaction setType(TransactionType type) {
        this.type = type;
        return this;
    }

    public @Nullable String getComments() {
        return comments;
    }

    public Transaction setComments(String comments) {
        this.comments = comments;
        return this;
    }
}
