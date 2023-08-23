package com.mineaurion.aurioneconomy.bukkit.vault;

import com.mineaurion.aurioneconomy.common.economyapi.AbstractCurrency;

public class Currency extends AbstractCurrency {
    public Currency(String singular, String plural, String symbol, int fractionDigits, boolean defaultCurrency){
        super(
                singular,
                plural,
                symbol,
                fractionDigits,
                defaultCurrency
        );
    }

    public String getDisplayName(){
        return getSingular();
    }

    public String getPluralDisplayName(){
        return getPlural();
    }
}
