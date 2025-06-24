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
package com.mycelium.wapi.wallet.eth.coins

import com.mycelium.wapi.wallet.Address
import com.mycelium.wapi.wallet.coins.CryptoCurrency
import com.mycelium.wapi.wallet.eth.EthAddress
import org.web3j.abi.datatypes.Address as W3jAddress
import org.web3j.crypto.WalletUtils

abstract class EthCoin(id: String?, name: String?, symbol: String?)
        : CryptoCurrency(id, name, symbol, 18, 8, false) {

    override fun parseAddress(addressString: String?): Address? = parseAddress(this, addressString)

    companion object {
        @JvmStatic val BLOCK_TIME_IN_SECONDS = 15

        fun parseAddress(cryptoCurrency: CryptoCurrency, addressString: String?): Address? = when {
            addressString == null -> null
            // additional wrap of addressString into Address is called upon to unify addresses with and
            // without '0x' prefix
            WalletUtils.isValidAddress(addressString) -> EthAddress(cryptoCurrency, W3jAddress(addressString).toString())
            else -> null
        }
    }
}