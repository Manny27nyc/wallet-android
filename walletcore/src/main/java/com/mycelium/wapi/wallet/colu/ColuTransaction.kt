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
package com.mycelium.wapi.wallet.colu

import com.mrd.bitlib.FeeEstimatorBuilder
import com.mrd.bitlib.model.BitcoinTransaction
import com.mycelium.wapi.wallet.Transaction
import com.mycelium.wapi.wallet.WalletAccount
import com.mycelium.wapi.wallet.btc.BtcAddress
import com.mycelium.wapi.wallet.coins.CryptoCurrency
import com.mycelium.wapi.wallet.coins.Value
import com.mycelium.wapi.wallet.colu.json.ColuBroadcastTxHex
import java.math.BigInteger


class ColuTransaction(type: CryptoCurrency?, val destination: BtcAddress?, val amount: Value?, val feePerKb: Value?)
    : Transaction(type) {
    var txHex: String? = null

    var fundingAddress: List<BtcAddress> = listOf()

    var baseTransaction: ColuBroadcastTxHex.Json? = null

    var transaction : BitcoinTransaction? = null

    val fundingAccounts = mutableListOf<WalletAccount<BtcAddress>>()

    override fun getEstimatedTransactionSize(): Int {
        return if (baseTransaction != null) {
            //TODO fix length
            baseTransaction?.txHex?.length!!
        } else {
            val estimatorBuilder = FeeEstimatorBuilder()
            val estimator = estimatorBuilder.setLegacyInputs(2)
                    .setLegacyOutputs(4)
                    .createFeeEstimator()
            estimator.estimateTransactionSize()
        }
    }

    override fun getId(): ByteArray? {
        return transaction?.id!!.bytes
    }

    override fun txBytes(): ByteArray? {
        return transaction?.toBytes()
    }

    override fun totalFee(): Value = feePerKb!!
        .times(estimatedTransactionSize.toBigInteger())
        .div(BigInteger.valueOf(1000))
}