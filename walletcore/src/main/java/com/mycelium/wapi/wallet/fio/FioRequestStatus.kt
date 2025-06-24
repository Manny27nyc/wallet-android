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
package com.mycelium.wapi.wallet.fio

enum class FioRequestStatus(val status: String) {
    REQUESTED("requested"),
    REJECTED("rejected"),
    SENT_TO_BLOCKCHAIN("sent_to_blockchain"),
    NONE("none");

    companion object {
        fun getStatus(status: String) = values().first { it.status == status }
    }
}
