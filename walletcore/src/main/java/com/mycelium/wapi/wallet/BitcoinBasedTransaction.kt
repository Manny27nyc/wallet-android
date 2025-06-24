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
package com.mycelium.wapi.wallet

import com.mrd.bitlib.UnsignedTransaction
import com.mrd.bitlib.model.BitcoinTransaction
import com.mycelium.wapi.wallet.coins.CryptoCurrency
import com.mycelium.wapi.wallet.coins.Value
import java.math.BigInteger

abstract class BitcoinBasedTransaction protected constructor(type: CryptoCurrency, val feePerKb: Value?) : Transaction(type) {
    override fun txBytes(): ByteArray? {
        return tx?.toBytes()
    }

    override fun getId(): ByteArray? {
        return tx?.id!!.bytes
    }

    override fun totalFee(): Value = feePerKb!!
        .times(estimatedTransactionSize.toBigInteger())
        .div(BigInteger.valueOf(1000))

    var tx: BitcoinTransaction? = null
    var unsignedTx: UnsignedTransaction? = null
}
