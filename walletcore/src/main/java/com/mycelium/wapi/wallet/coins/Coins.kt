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
package com.mycelium.wapi.wallet.coins

import com.mycelium.wapi.api.lib.CurrencyCode
import com.mycelium.wapi.wallet.bch.coins.BchMain
import com.mycelium.wapi.wallet.bch.coins.BchTest
import com.mycelium.wapi.wallet.btc.coins.BitcoinMain
import com.mycelium.wapi.wallet.btc.coins.BitcoinTest
import com.mycelium.wapi.wallet.btcvault.coins.BitcoinVaultMain
import com.mycelium.wapi.wallet.btcvault.coins.BitcoinVaultTest
import com.mycelium.wapi.wallet.colu.coins.*
import com.mycelium.wapi.wallet.eth.coins.EthMain
import com.mycelium.wapi.wallet.eth.coins.EthTest
import com.mycelium.wapi.wallet.fiat.coins.FiatType
import com.mycelium.wapi.wallet.fio.coins.FIOMain
import com.mycelium.wapi.wallet.fio.coins.FIOTest
import java.util.*

val COINS_SET = setOf(
        BitcoinMain, BitcoinTest,
        BchMain, BchTest,
        EthMain, EthTest,
        FIOMain, FIOTest,
        MASSCoin, MASSCoinTest,
        MTCoin, MTCoinTest,
        RMCCoin, RMCCoinTest,
        BitcoinVaultMain, BitcoinVaultTest
)

val COINS = COINS_SET.associateBy { it.id }

val SYMBOL_COIN_MAP = COINS_SET.associateBy { it.symbol.toUpperCase(Locale.US) }

fun String.toAssetInfo(): AssetInfo? =
        when {
            CurrencyCode.values().find { it.shortString.equals(this, true) } != null -> FiatType(this)
            else -> COINS.values.find { it.getName().equals(this, true) || it.getSymbol().equals(this, true) }
        }