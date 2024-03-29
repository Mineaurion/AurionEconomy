package com.mineaurion.aurioneconomy.common.economyapi;

import com.mineaurion.aurioneconomy.common.plugin.AurionEconomyPlugin;

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

    public Currency(){
        this(
                "Aurion",
                "Aurions",
                "A",
                0,
                true
        );
    }

    public String getId(){
        return AurionEconomyPlugin.MOD_ID + ":" + getSingular();
    }

    public String getSingular() {
        return singular;
    }

    public void setSingular(String singular) {
        this.singular = singular;
    }

    public String getPlural() {
        return plural;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getFractionDigits() {
        return fractionDigits;
    }

    public void setFractionDigits(int fractionDigits) {
        this.fractionDigits = fractionDigits;
    }

    public boolean isDefaultCurrency() {
        return defaultCurrency;
    }

    public void setDefaultCurrency(boolean defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}
