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
package com.mycelium.wapi.wallet.btcvault.coins

import com.mycelium.wapi.wallet.Address
import com.mycelium.wapi.wallet.btcvault.BtcvAddress
import com.mycelium.wapi.wallet.coins.CryptoCurrency


object BitcoinVaultMain : CryptoCurrency("bitcoinvault.main", "BitcoinVault", "BTCV", 8, 2, true) {
    override fun parseAddress(addressString: String?): Address? {
        val address = BtcvAddress.fromString(this, addressString) ?: return null

        try {
            if (!address.network!!.isProdnet) {
                return null
            }
        } catch (e: IllegalStateException) {
            return null
        }
        return address
    }
}