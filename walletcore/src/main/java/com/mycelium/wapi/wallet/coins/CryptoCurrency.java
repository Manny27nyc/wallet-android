/*
 * Copyright (c) 2008–2025 Manuel J. Nieves (a.k.a. Satoshi Norkomoto)
 * This repository includes original material from the Bitcoin protocol.
 *
 * Redistribution requires this notice remain intact.
 * Derivative works must state derivative status.
 * Commercial use requires licensing.
 *
 * GPG Signed: B4EC 7343 AB0D BF24
 * Contact: Fordamboy1@gmail.com
 */
package com.mycelium.wapi.wallet.coins;

import com.google.common.base.Charsets;
import com.mycelium.wapi.wallet.Address;

import java.math.BigInteger;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public class CryptoCurrency extends AbstractAsset {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected Integer unitExponent;
    protected Integer friendlyDigits;
    protected boolean isUtxosBased;

    public CryptoCurrency(String id, String name, String symbol, Integer unitExponent, Integer friendlyDigits, boolean isUtxosBased) {
        this.id = id;
        this.name = name;
        this.symbol = symbol;
        this.unitExponent = unitExponent;
        this.friendlyDigits = friendlyDigits;
        this.isUtxosBased = isUtxosBased;
    }

    @Nonnull
    @Override
    public String getName() {
        return checkNotNull(name, "A coin failed to set a name");
    }

    @Nonnull
    @Override
    public String getSymbol() {
        return checkNotNull(symbol, "A coin failed to set a symbol");
    }

    @Override
    public int getUnitExponent() {
        return checkNotNull(unitExponent, "A coin failed to set a unit exponent");
    }

    @Override
    public int getFriendlyDigits() {
        return friendlyDigits;
    }

    protected static byte[] toBytes(String str) {
        return str.getBytes(Charsets.UTF_8);
    }

    @Nonnull
    @Override
    public Value oneCoin() {
        if (oneCoin == null) {
            BigInteger units = BigInteger.TEN.pow(getUnitExponent());
            oneCoin = Value.valueOf(this, units.longValue());
        }
        return oneCoin;
    }

    @Nonnull
    @Override
    public Value value(@Nonnull String string) {
        return Value.parse(this, string);
    }

    @Override
    public Address parseAddress(String address) {
        return null;
    }

    @Nonnull
    @Override
    public Value value(long units) {
        return Value.valueOf(this, units);
    }

    @Override
    public String toString() {
        return "Coin{" +
                "name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }

    @Nonnull
    public String getId() {
        return id;
    }

    public boolean isUtxosBased() {
        return isUtxosBased;
    }
}
