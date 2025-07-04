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

import java.math.BigDecimal;
import java.util.List;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

/*
"previousOutput":{"asm":"OP_DUP OP_HASH160 1cebfabf0a1201fe29a126743511bf4a56c51c83 OP_EQUALVERIFY OP_CHECKSIG",
        "hex":"76a9141cebfabf0a1201fe29a126743511bf4a56c51c8388ac",
        "reqSigs":1,
        "type":"pubkeyhash",
        "addresses":["13dvcWk3HjbgjYjyakfDBcbPco5J5CZi5x"]},
*/

public class PreviousOutput {
    public static class Json extends GenericJson {

        @Key
        public String asm;

        @Key
        public int reqSigs;

        @Key
        public String type;

        @Key
        public List<String> addresses;
    }
}
