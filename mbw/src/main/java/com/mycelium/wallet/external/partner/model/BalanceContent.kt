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
package com.mycelium.wallet.external.partner.model

import com.google.gson.annotations.SerializedName
import com.mycelium.wapi.wallet.Util
import com.mycelium.wapi.wallet.WalletAccount
import java.io.Serializable


data class BalanceContent(@SerializedName("buy-sell-buttons") val buttons: List<BuySellButton>,
                          @SerializedName("buy-sell-quads") val quads: List<BuySellButton>)

data class Filter(val currency: List<String> = listOf())
data class BuySellButton(val name: String?,
                         val iconUrl: String?,
                         val link: String?,
                         val index: Int?,
                         val filter: Filter?) : CommonContent(), Serializable

fun Filter?.check(account: WalletAccount<*>) =
    if (this == null) true
    else currency.contains(Util.trimTestnetSymbolDecoration(account.coinType.symbol))