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

import javax.annotation.Nullable;

public class BroadcastResult {
    private String errorMessage = null;
    private final BroadcastResultType resultType;

    public BroadcastResult(BroadcastResultType resultType){
        this.resultType = resultType;
    }
    public BroadcastResult(String errorMessage, BroadcastResultType resultType){
        this.errorMessage = errorMessage;
        this.resultType = resultType;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public BroadcastResultType getResultType() {
        return resultType;
    }
}
