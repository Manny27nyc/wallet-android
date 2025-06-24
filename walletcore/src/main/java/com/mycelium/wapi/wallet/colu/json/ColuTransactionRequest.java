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

import java.util.List;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

public class ColuTransactionRequest {
    public static class Json extends GenericJson {
        @Key
        public long fee;

        @Key
        public List<String> from;

        @Key
        public List<String> sendutxo;

        @Key
        public String financeOutputTxid;

        @Key
        public List<ColuTxDest.Json> to;

        @Key
        public ColuTxFlags.Json flags;
    }
}
