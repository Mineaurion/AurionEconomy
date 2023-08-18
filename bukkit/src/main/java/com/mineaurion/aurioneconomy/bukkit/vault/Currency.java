package com.mineaurion.aurioneconomy.bukkit.vault;

public class Currency {

    private String singular;
    private String plural;
    private String symbol;
    private int fractionDigits;
    private boolean defaultCurrency;

    public Currency(String singular, String plural, String symbol, int fractionDigits, boolean defaultCurrency){
        this.singular = singular;
        this.plural = plural;
        this.symbol = symbol;
        this.fractionDigits = fractionDigits;
        this.defaultCurrency = defaultCurrency;
    }

    public String getId(){
        return "aurionseconomy:" + singular;
    }

    public String getName(){
        return singular;
    }

    public String getDisplayName(){
        return singular;
    }

    public String getPluralDisplayName(){
        return plural;
    }

    public int getFractionDigits(){
        return fractionDigits;
    }

    public boolean isDefault(){
        return defaultCurrency;
    }
}
