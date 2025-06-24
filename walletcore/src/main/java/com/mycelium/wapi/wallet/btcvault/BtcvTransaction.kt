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

import com.mrd.bitlib.FeeEstimatorBuilder
import com.mrd.bitlib.model.BitcoinTransaction
import com.mycelium.wapi.wallet.BitcoinBasedTransaction
import com.mycelium.wapi.wallet.coins.CryptoCurrency
import com.mycelium.wapi.wallet.coins.Value
import java.io.Serializable

class BtcvTransaction(type: CryptoCurrency, val destination: BtcvAddress?, val amount: Value?, feePerKb: Value?)
    : BitcoinBasedTransaction(type, feePerKb), Serializable {

    constructor(coinType: CryptoCurrency, tx: BitcoinTransaction): this (coinType, null, null, null) {
        setTransaction(tx)
    }

    fun setTransaction(tx: BitcoinTransaction) {
        this.tx = tx
        this.isSigned = true
    }

    override fun getEstimatedTransactionSize(): Int {
        val estimatorBuilder = FeeEstimatorBuilder()
        val estimator = if (unsignedTx != null) {
            estimatorBuilder.setArrayOfInputs(unsignedTx!!.fundingOutputs)
                    .setArrayOfOutputs(unsignedTx!!.outputs)
                    .createFeeEstimator()
        } else {
            estimatorBuilder.setLegacyInputs(1)
                    .setLegacyOutputs(2)
                    .createFeeEstimator()
        }
        return estimator.estimateTransactionSize()
    }
}