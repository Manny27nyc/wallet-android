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
package com.mycelium.wapi.wallet.genericdb

import com.mycelium.generated.wallet.database.FeeEstimation
import com.mycelium.generated.wallet.database.WalletDB
import com.mycelium.wapi.wallet.FeeEstimationsGeneric
import com.mycelium.wapi.wallet.coins.AssetInfo

class FeeEstimationsBacking(walletDB: WalletDB) {
    private val queries = walletDB.feeEstimationsQueries

    fun getEstimationForCurrency(currency: AssetInfo): FeeEstimationsGeneric? =
        queries.selectByCurrency(currency).executeAsOneOrNull()?.let {
            FeeEstimationsGeneric(it.low, it.economy, it.normal, it.high, it.lastCheck, it.scale)
        }

    fun updateFeeEstimation(estimation: FeeEstimation) {
        queries.insertFullObject(estimation)
    }
}