package com.mineaurion.aurioneconomy.common.model;

public enum TransactionType {
    ADD("add"),
    SET("set"),
    WITHDRAW("withdraw"),
    PLAYER_TO_PLAYER("player_to_player")
    ;

    private final String value;

    TransactionType(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
