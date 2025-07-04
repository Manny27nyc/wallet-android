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

import com.mycelium.wapi.wallet.Address;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface AssetInfo extends Serializable {
    @Nonnull String getId();
    @Nonnull String getName();
    @Nonnull String getSymbol();
    int getUnitExponent();
    int getFriendlyDigits();

    /**
     * Typical 1 coin value, like 1 Bitcoin, 1 Peercoin or 1 Dollar
     */
    @Nonnull Value oneCoin();

    @Nonnull Value value(long units);

    @Nonnull Value value(@Nonnull String string);

    Address parseAddress(@Nullable String address);
}
