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

import com.mycelium.wallet.MinerFee;

public class FeeLvlItem {
    public MinerFee minerFee;
    public String duration;
    public int type;

    public FeeLvlItem(MinerFee minerFee, String duration, int type) {
        this.minerFee = minerFee;
        this.duration = duration;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FeeLvlItem that = (FeeLvlItem) o;

        if (type != that.type) return false;
        return minerFee == that.minerFee;

    }

    @Override
    public int hashCode() {
        int result = minerFee != null ? minerFee.hashCode() : 0;
        result = 31 * result + type;
        return result;
    }
}
