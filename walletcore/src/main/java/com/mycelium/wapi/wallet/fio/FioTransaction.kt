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

import com.mycelium.wapi.wallet.Transaction
import com.mycelium.wapi.wallet.coins.CryptoCurrency
import com.mycelium.wapi.wallet.coins.Value

class FioTransaction(type: CryptoCurrency, val toAddress: String, val value: Value, val fee: Value) : Transaction(type) {
    var txId: ByteArray? = null
    override fun getId() = txId

    override fun txBytes(): ByteArray = ByteArray(0)
    override fun totalFee(): Value = fee

    override fun getEstimatedTransactionSize(): Int = 1
}