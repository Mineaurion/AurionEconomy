package com.mineaurion.aurioneconomy.common.model;

import java.util.UUID;

public class Account {

    private String uuid;
    private int balance;

    public Account(String uuid, int balance){
        this.uuid = uuid;
        this.balance = balance;
    }

    public String getUuid() {
        return uuid;
    }

    public UUID getUUID(){
        return UUID.fromString(uuid);
    }

    public Account setUuid(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public int getBalance() {
        return balance;
    }

    public Account setBalance(int balance) {
        this.balance = balance;
        return this;
    }
}
