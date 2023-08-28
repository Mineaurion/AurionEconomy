package com.mineaurion.aurioneconomy.common.economyapi;

public abstract class AbstractCurrency {
    private String singular;
    private String plural;
    private String symbol;
    private int fractionDigits;
    private boolean defaultCurrency;

    public AbstractCurrency(String singular, String plural, String symbol, int fractionDigits, boolean defaultCurrency){
        this.singular = singular;
        this.plural = plural;
        this.symbol = symbol;
        this.fractionDigits = fractionDigits;
        this.defaultCurrency = defaultCurrency;
    }

    public String getId(){
        return "aurioneconomy:" + getSingular();
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
