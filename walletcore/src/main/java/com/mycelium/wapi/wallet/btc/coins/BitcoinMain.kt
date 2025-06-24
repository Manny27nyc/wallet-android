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
package com.mycelium.wapi.wallet.btc.coins

import com.mrd.bitlib.model.BitcoinAddress
import com.mycelium.wapi.wallet.Address
import com.mycelium.wapi.wallet.btc.BtcAddress
import com.mycelium.wapi.wallet.coins.CryptoCurrency

object BitcoinMain : CryptoCurrency("bitcoin.main", "Bitcoin", "BTC", 8, 8, true) {
    override fun parseAddress(addressString: String?): Address? {
        val address = BitcoinAddress.fromString(addressString) ?: return null
        try {
            if (!address.network.isProdnet) {
                return null
            }
        } catch (e: IllegalStateException) {
            return null
        }
        return BtcAddress(this, address)
    }

    @JvmStatic
    fun get() = this
}