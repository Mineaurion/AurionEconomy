package com.mineaurion.aurioneconomy.sponge.eco;

import com.mineaurion.aurioneconomy.common.economyapi.AbstractCurrency;
import net.kyori.adventure.text.Component;

import java.math.BigDecimal;

public class CurrencyImpl extends AbstractCurrency implements org.spongepowered.api.service.economy.Currency {

    public CurrencyImpl(String singular, String plural, String symbol, int fractionDigits, boolean defaultCurrency){
        super(
                singular,
                plural,
                symbol,
                fractionDigits,
                defaultCurrency
        );
    }
    @Override
    public Component displayName() {
        return Component.text(getSingular());
    }

    @Override
    public Component pluralDisplayName() {
        return Component.text(getPlural());
    }

    @Override
    public Component symbol() {
        return Component.text(getSymbol());
    }

    @Override
    public Component format(BigDecimal amount, int numFractionDigits) {
        return Component.text(amount.intValue());
    }

    @Override
    public int defaultFractionDigits() {
        return getFractionDigits();
    }

    @Override
    public boolean isDefault() {
        return isDefaultCurrency();
    }
}
