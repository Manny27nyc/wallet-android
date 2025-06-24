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
package com.mycelium.wapi.wallet;

import com.mycelium.wapi.wallet.coins.CryptoCurrency;
import com.mycelium.wapi.wallet.coins.Value;

import java.io.Serializable;

public abstract class Transaction implements Serializable {

    /**
     * Type of cryptocurrency the transaction operates with
     */
    public CryptoCurrency type;

    public boolean isSigned;

    protected Transaction(CryptoCurrency type) {
        this.type = type;
        this.isSigned = false;
    }

    public abstract byte[] getId();

    public abstract byte[] txBytes();

    public abstract int getEstimatedTransactionSize();

    public abstract Value totalFee();
}
