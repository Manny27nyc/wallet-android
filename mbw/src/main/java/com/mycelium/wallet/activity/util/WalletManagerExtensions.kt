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
package com.mycelium.wallet.activity.util

import com.mycelium.wallet.Utils
import com.mycelium.wallet.exchange.ValueSum
import com.mycelium.wapi.wallet.WalletAccount
import com.mycelium.wapi.wallet.WalletManager
import com.mycelium.wapi.wallet.btc.single.SingleAddressAccount
import com.mycelium.wapi.wallet.colu.getColuAccounts

/**
 * Get bitcoin single account list, excluding accounts linked with colu account
 *
 * @return list of accounts
 */
fun WalletManager.getBTCSingleAddressAccounts() = getAccounts().filter { it is SingleAddressAccount
        && !Utils.checkIsLinked(it, getColuAccounts()) && it.isVisible() && !it.toRemove}

fun WalletManager.getActiveBTCSingleAddressAccounts() = getAccounts().filter { it is SingleAddressAccount
        && !Utils.checkIsLinked(it, getColuAccounts()) && it.isVisible() && !it.toRemove && it.isActive }

fun List<WalletAccount<*>>.getSpendableBalance(): ValueSum =
        ValueSum().also { sum ->
            this.filter { it.isActive }
                    .forEach {
                        sum.add(it.accountBalance.spendable)
                    }
        }