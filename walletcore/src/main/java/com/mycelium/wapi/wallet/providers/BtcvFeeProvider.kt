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
package com.mycelium.wapi.wallet.providers

import com.mycelium.wapi.Constants.BTC_ELECTRUMX_FEE_ESTIMATIONS_SCALE
import com.mycelium.wapi.api.Wapi
import com.mycelium.wapi.wallet.FeeEstimationsGeneric
import com.mycelium.wapi.wallet.btcvault.coins.BitcoinVaultMain
import com.mycelium.wapi.wallet.btcvault.coins.BitcoinVaultTest
import com.mycelium.wapi.wallet.coins.Value
import com.mycelium.wapi.wallet.genericdb.FeeEstimationsBacking

class BtcvFeeProvider(testnet: Boolean, wapi: Wapi, feeBacking: FeeEstimationsBacking) :
        WapiFeeProvider(wapi, feeBacking) {
    override val coinType = if (testnet) {
        BitcoinVaultTest
    } else {
        BitcoinVaultMain
    }

    override var estimation = feeBacking.getEstimationForCurrency(coinType)
            ?: FeeEstimationsGeneric(Value.valueOf(coinType, 1000),
                    Value.valueOf(coinType, 3000),
                    Value.valueOf(coinType, 6000),
                    Value.valueOf(coinType, 8000),
                    0,
                    BTC_ELECTRUMX_FEE_ESTIMATIONS_SCALE
            )
}