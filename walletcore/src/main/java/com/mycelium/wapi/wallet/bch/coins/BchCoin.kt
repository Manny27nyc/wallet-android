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
package com.mycelium.wapi.wallet.bch.coins

import com.mrd.bitlib.model.BitcoinAddress
import com.mycelium.wapi.wallet.Address
import com.mycelium.wapi.wallet.btc.BtcAddress
import com.mycelium.wapi.wallet.btc.coins.BitcoinMain
import com.mycelium.wapi.wallet.btc.coins.BitcoinTest
import com.mycelium.wapi.wallet.coins.CryptoCurrency

abstract class BchCoin(id: String?, name: String?, symbol: String?, unitExponent: Int?, friendlyDigits: Int?, isUtxosBased: Boolean)
        : CryptoCurrency(id, name, symbol, unitExponent, friendlyDigits, isUtxosBased){

    override fun parseAddress(addressString: String?): Address {
        val address = BitcoinAddress.fromString(addressString)
        return BtcAddress(if (address.network.isProdnet) BitcoinMain else BitcoinTest, address)
    }
}