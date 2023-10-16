package com.mineaurion.aurioneconomy.sponge.eco;

import com.mineaurion.aurioneconomy.common.economyapi.Currency;
import net.kyori.adventure.text.Component;

import java.math.BigDecimal;

public class CurrencyImpl extends Currency implements org.spongepowered.api.service.economy.Currency {

    public CurrencyImpl(){
        super();
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
