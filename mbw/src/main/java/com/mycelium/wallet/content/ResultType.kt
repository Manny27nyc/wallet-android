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
package com.mycelium.wallet.content


enum class ResultType {
    ADDRESS, ADDRESS_STRING, PRIVATE_KEY, HD_NODE, ACCOUNT,
    NONE,
    ASSET_URI, URI, SHARE, MASTER_SEED, POP_REQUEST, BIT_ID_REQUEST,
    WORD_LIST
}