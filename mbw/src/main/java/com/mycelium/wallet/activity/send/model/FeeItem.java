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
package com.mycelium.wallet.activity.send.model;

import com.mycelium.wallet.activity.send.view.SelectableRecyclerView;
import com.mycelium.wapi.wallet.coins.Value;

public class FeeItem {
    public long feePerKb;
    public Value value; // Fee value in minimal asset's units
    public Value fiatValue;
    /** as defined in {@link SelectableRecyclerView.SRVAdapter} */
    public int type;

    public FeeItem(long feePerKb, Value value, Value fiatValue, int type) {
        this.feePerKb = feePerKb;
        this.value = value;
        this.fiatValue = fiatValue;
        this.type = type;
    }

    public FeeItem(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeeItem feeItem = (FeeItem) o;

        if (feePerKb != feeItem.feePerKb) return false;
        return type == feeItem.type;
    }

    @Override
    public int hashCode() {
        int result = (int) (feePerKb ^ (feePerKb >>> 32));
        result = 31 * result + type;
        return result;
    }
}
