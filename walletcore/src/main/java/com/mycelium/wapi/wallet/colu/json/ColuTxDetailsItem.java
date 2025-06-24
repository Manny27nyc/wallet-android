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
package com.mycelium.wapi.wallet.colu.json;

import com.mrd.bitlib.model.BitcoinAddress;
import com.mycelium.wapi.model.TransactionDetails;

import java.math.BigDecimal;

public class ColuTxDetailsItem extends TransactionDetails.Item {
    private final long assetAmount;

    private final int scale;

    public ColuTxDetailsItem(BitcoinAddress address, long value, boolean isCoinbase, long assetAmount, int scale) {
        super(address, value, isCoinbase);
        this.assetAmount = assetAmount;
        this.scale = scale;
    }

    public BigDecimal getAmount() {
        return BigDecimal.valueOf(assetAmount, scale);
    }
}
