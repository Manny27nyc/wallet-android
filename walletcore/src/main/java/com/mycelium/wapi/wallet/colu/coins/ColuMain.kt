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
package com.mycelium.wapi.wallet.colu.coins

import com.mrd.bitlib.model.BitcoinAddress
import com.mrd.bitlib.model.AddressType
import com.mycelium.wapi.wallet.Address
import com.mycelium.wapi.wallet.btc.BtcAddress
import com.mycelium.wapi.wallet.coins.CryptoCurrency

abstract class ColuMain(id: String?, name: String?, symbol: String?, unitExponent: Int?, friendlyDigits: Int?)
    : CryptoCurrency(id, name, symbol, unitExponent, friendlyDigits, true) {

    override fun getName(): String = name

    override fun getSymbol(): String = symbol

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is ColuMain) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }

    override fun parseAddress(addressString: String?): Address? {
        val address = BitcoinAddress.fromString(addressString) ?: return null

        try {
            if (address.type === AddressType.P2WPKH)
                return null
        } catch (e: IllegalStateException) {
            return null
        }
        return BtcAddress(this, address)
    }
}
