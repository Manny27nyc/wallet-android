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
package com.mycelium.wallet.activity.util.accountstrategy;

/**
 * Strategy to unify accounts displaying duty.
 */
public interface AccountDisplayStrategy {
    /**
     * Returns account type label. For instance "bitcoin", "bitcoincash"...
     */
    String getLabel();

    /**
     * Returns currency name. For instance "Bitcoin", "Bitcoin Cash".
     */
    String getCurrencyName();

    /**
     * Returns input hint. For instance "0.00 BTC", "0.00 MSS".
     */
    String getHint();
}
