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
package com.mycelium.wapi.wallet.btcvault

import com.megiontechnologies.Bitcoins

/**
 * address - The address to send funds to
 * amount - The amount to send measured in satoshis
 */

class BtcvReceiver(var address: BtcvAddress, var amount: Long = 0) {
    private val serialVersionUID = 1L

    constructor(address: BtcvAddress, amount: Bitcoins) : this(address, amount.longValue)
}